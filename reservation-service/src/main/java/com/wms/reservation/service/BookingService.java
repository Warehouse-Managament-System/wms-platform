package com.wms.reservation.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.dto.RoomAvailabilityResponse;
import com.wms.common.enums.BookingStatus;
import com.wms.common.enums.BookingType;
import com.wms.common.event.BookingCancelledEvent;
import com.wms.common.event.BookingConfirmedEvent;
import com.wms.common.event.BookingExpiredEvent;
import com.wms.common.event.BookingExpirySoonEvent;
import com.wms.common.event.KafkaTopics;
import com.wms.common.exception.AvailabilityException;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.reservation.dto.BookingResponse;
import com.wms.reservation.dto.BookingSpecification;
import com.wms.reservation.dto.CreateBookingRequest;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.feign.InventoryClient;
import com.wms.reservation.repository.BookingRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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

      java.util.List<BookingStatus> activeStatuses =
          java.util.List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE);

      if (roomId != null
          && bookingRepository.existsByRoomIdAndStatusInAndDeletedAtIsNull(
              roomId, activeStatuses)) {
        throw new AvailabilityException("Room already has an active booking");
      }

      if (zoneId != null
          && bookingRepository.existsByZoneIdAndStatusInAndDeletedAtIsNull(
              zoneId, activeStatuses)) {
        throw new AvailabilityException("Zone already has an active booking");
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
  public BookingResponse create(UUID customerId, CreateBookingRequest request) {
    Booking booking =
        create(
            customerId,
            request.bookingType(),
            request.warehouseId(),
            request.roomId(),
            request.zoneId(),
            request.startDate(),
            request.endDate());
    return BookingResponse.from(booking);
  }

  @Transactional(readOnly = true)
  public PageResponse<BookingResponse> listByCustomer(
      UUID customerId, BookingStatus status, Pageable pageable) {
    Specification<Booking> spec =
        BookingSpecification.hasCustomerId(customerId).and(BookingSpecification.isNotDeleted());
    if (status != null) {
      spec = spec.and(BookingSpecification.hasStatus(status));
    }
    return PageResponse.from(bookingRepository.findAll(spec, pageable).map(BookingResponse::from));
  }

  @Transactional(readOnly = true)
  public PageResponse<BookingResponse> listByWarehouse(
      UUID warehouseId, BookingStatus status, Pageable pageable) {
    Specification<Booking> spec =
        BookingSpecification.hasWarehouseId(warehouseId).and(BookingSpecification.isNotDeleted());
    if (status != null) {
      spec = spec.and(BookingSpecification.hasStatus(status));
    }
    return PageResponse.from(bookingRepository.findAll(spec, pageable).map(BookingResponse::from));
  }

  @Transactional(readOnly = true)
  public BookingResponse getById(UUID id, UUID customerId) {
    Booking booking =
        bookingRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));
    if (!booking.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("Booking", id);
    }
    return BookingResponse.from(booking);
  }

  @Transactional
  public BookingResponse cancel(UUID id, UUID customerId) {
    Booking booking = cancelInternal(id, customerId, "Cancelled by customer");
    return BookingResponse.from(booking);
  }

  @Transactional
  public BookingResponse confirm(UUID id) {
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
        KafkaTopics.BOOKING_CONFIRMED,
        new BookingConfirmedEvent(
            booking.getId(),
            booking.getCustomerId(),
            booking.getRoomId(),
            booking.getStartDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
            booking.getEndDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));

    return BookingResponse.from(booking);
  }

  @Transactional
  public BookingResponse reject(UUID id, String reason) {
    Booking booking =
        bookingRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    if (booking.getStatus() != BookingStatus.PENDING) {
      throw new BusinessRuleException("Only pending bookings can be rejected");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    booking = bookingRepository.save(booking);

    outboxPublisher.publish(
        "Booking",
        booking.getId(),
        KafkaTopics.BOOKING_CANCELLED,
        new BookingCancelledEvent(booking.getId(), booking.getCustomerId(), reason));

    return BookingResponse.from(booking);
  }

  private Booking cancelInternal(UUID id, UUID customerId, String reason) {
    Booking booking =
        bookingRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    if (!booking.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("Booking", id);
    }

    if (booking.getStatus() != BookingStatus.PENDING
        && booking.getStatus() != BookingStatus.CONFIRMED) {
      throw new BusinessRuleException("Only pending or confirmed bookings can be cancelled");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    booking = bookingRepository.save(booking);

    outboxPublisher.publish(
        "Booking",
        booking.getId(),
        KafkaTopics.BOOKING_CANCELLED,
        new BookingCancelledEvent(booking.getId(), booking.getCustomerId(), reason));

    return booking;
  }

  @Scheduled(cron = "0 0 2 * * *")
  @Transactional
  public void checkExpiringSoon() {
    LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
    List<Booking> expiringSoon =
        bookingRepository.findByEndDateAndStatusAndDeletedAtIsNull(
            sevenDaysFromNow, BookingStatus.ACTIVE);

    for (Booking booking : expiringSoon) {
      Instant expiryDate = booking.getEndDate().atStartOfDay().toInstant(ZoneOffset.UTC);

      outboxPublisher.publish(
          "Booking",
          booking.getId(),
          KafkaTopics.BOOKING_EXPIRY_SOON,
          new BookingExpirySoonEvent(booking.getId(), booking.getCustomerId(), expiryDate));

      log.info("Expiry-soon notification sent for booking: {}", booking.getId());
    }
  }

  @Scheduled(cron = "0 0 3 * * *")
  @Transactional
  public void expireBookings() {
    LocalDate today = LocalDate.now();
    List<Booking> expired =
        bookingRepository.findByEndDateBeforeAndStatusAndDeletedAtIsNull(
            today, BookingStatus.ACTIVE);

    for (Booking booking : expired) {
      booking.setStatus(BookingStatus.EXPIRED);
      bookingRepository.save(booking);

      outboxPublisher.publish(
          "Booking",
          booking.getId(),
          KafkaTopics.BOOKING_EXPIRED,
          new BookingExpiredEvent(booking.getId(), booking.getCustomerId()));

      log.info("Booking expired: {}", booking.getId());
    }
  }
}
