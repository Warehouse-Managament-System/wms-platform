package com.wms.reservation.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.dto.RoomAvailabilityResponse;
import com.wms.common.enums.BookingStatus;
import com.wms.common.enums.BookingType;
import com.wms.common.event.BookingConfirmedEvent;
import com.wms.common.exception.AvailabilityException;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.reservation.dto.BookingResponse;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.feign.InventoryClient;
import com.wms.reservation.repository.BookingRepository;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingLockService bookingLockService;
  private final PriceCalculationService priceCalculationService;
  private final InventoryClient inventoryClient;
  private final InvoiceService invoiceService;
  private final OutboxPublisher outboxPublisher;

  @Transactional
  public Booking create(
      UUID customerId,
      BookingType bookingType,
      UUID warehouseId,
      UUID roomId,
      UUID zoneId,
      java.time.LocalDate startDate,
      java.time.LocalDate endDate) {

    if (!endDate.isAfter(startDate)) {
      throw new BusinessRuleException("End date must be after start date");
    }

    if (roomId == null && zoneId == null) {
      throw new BusinessRuleException("Either roomId or zoneId must be provided");
    }

    String lockKey = null;
    try {
      if (roomId != null) {
        lockKey = bookingLockService.acquireLock("room", roomId, Duration.ofSeconds(10));
      } else {
        lockKey = bookingLockService.acquireLock("zone", zoneId, Duration.ofSeconds(10));
      }

      RoomAvailabilityResponse availability =
          inventoryClient.checkRoomAvailability(
              roomId != null ? roomId : zoneId, startDate, endDate);

      if (!availability.available()) {
        throw new AvailabilityException(
            "Resource is not available for the requested dates: " + availability.reason());
      }

      java.math.BigDecimal totalPrice =
          priceCalculationService.calculateTotalPrice(
              startDate,
              endDate,
              availability.surfaceArea(),
              availability.pricePerSqmDaily(),
              availability.pricePerSqmWeekly(),
              availability.pricePerSqmMonthly(),
              availability.zoneDiscountPercentage(),
              availability.warehouseDiscountPercentage());

      Booking booking =
          Booking.builder()
              .customerId(customerId)
              .warehouseId(warehouseId)
              .bookingType(bookingType)
              .roomId(roomId)
              .zoneId(zoneId)
              .startDate(startDate)
              .endDate(endDate)
              .surfaceArea(availability.surfaceArea())
              .totalPrice(totalPrice)
              .status(BookingStatus.PENDING)
              .build();

      return bookingRepository.save(booking);
    } finally {
      bookingLockService.releaseLock(lockKey);
    }
  }

  @Transactional
  public BookingResponse confirm(UUID id, UUID ownerId) {
    Booking booking =
        bookingRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new BusinessRuleException("Only pending bookings can be confirmed");
    }

    booking.setStatus(BookingStatus.CONFIRMED);
    booking = bookingRepository.save(booking);

    invoiceService.generateBookingInvoice(booking);

    outboxPublisher.publish(
        "Booking",
        booking.getId(),
        "booking.confirmed",
        new BookingConfirmedEvent(
            booking.getId(),
            booking.getCustomerId(),
            booking.getRoomId(),
            booking.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC),
            booking.getEndDate().atStartOfDay().toInstant(ZoneOffset.UTC)));

    return BookingResponse.from(booking);
  }

  @Transactional
  public Booking reject(UUID id, UUID ownerId) {
    Booking booking =
        bookingRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new BusinessRuleException("Only pending bookings can be rejected");
    }

    booking.setStatus(BookingStatus.REJECTED);
    return bookingRepository.save(booking);
  }

  @Transactional(readOnly = true)
  public PageResponse<BookingResponse> listByCustomer(
      UUID customerId, BookingStatus status, org.springframework.data.domain.Pageable pageable) {
    var page =
        (status == null)
            ? bookingRepository.findByCustomerId(customerId, pageable)
            : bookingRepository.findByCustomerIdAndStatus(customerId, status, pageable);

    return PageResponse.from(page.map(BookingResponse::from));
  }

  @Transactional(readOnly = true)
  public BookingResponse getById(UUID id, UUID customerId) {
    Booking booking =
        bookingRepository
            .findByIdAndCustomerIdAndDeletedAtIsNull(id, customerId)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    return BookingResponse.from(booking);
  }

  @Transactional
  public BookingResponse cancel(UUID id, UUID customerId) {
    Booking booking =
        bookingRepository
            .findByIdAndCustomerIdAndDeletedAtIsNull(id, customerId)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new BusinessRuleException("Only pending bookings can be cancelled");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    booking = bookingRepository.save(booking);

    return BookingResponse.from(booking);
  }

  @Transactional(readOnly = true)
  public PageResponse<BookingResponse> listByWarehouse(
      UUID warehouseId, BookingStatus status, org.springframework.data.domain.Pageable pageable) {
    var page =
        (status == null)
            ? bookingRepository.findByWarehouseId(warehouseId, pageable)
            : bookingRepository.findByWarehouseIdAndStatus(warehouseId, status, pageable);

    return PageResponse.from(page.map(BookingResponse::from));
  }
}
