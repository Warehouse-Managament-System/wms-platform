package com.wms.delivery.service;

import com.wms.common.dto.GoodsItemAvailabilityResponse;
import com.wms.common.enums.DeliveryStatus;
import com.wms.common.event.DeliveryAcknowledgedEvent;
import com.wms.common.event.DeliveryConfirmedEvent;
import com.wms.common.event.KafkaTopics;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.UnauthorizedException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.delivery.dto.delivery.DeliveryRequestResponse;
import com.wms.delivery.entity.*;
import com.wms.delivery.feign.BookingClient;
import com.wms.delivery.feign.InventoryClient;
import com.wms.delivery.feign.dto.BookingStatusResponse;
import com.wms.delivery.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryRequestService {

  private final DeliveryRequestRepository requestRepository;
  private final DeliveryRequestItemRepository itemRepository;
  private final BookingClient bookingClient;
  private final InventoryClient inventoryClient;
  private final OutboxPublisher outboxPublisher;

  // ── Create ──────────────────────────────────────────────────────────────

  @Transactional
  public UUID createRequest(
      UUID bookingId,
      UUID customerId,
      String address,
      String city,
      String country,
      LocalDate requestedDate) {

    BookingStatusResponse booking = fetchBookingOrThrow(bookingId);

    if (!booking.customerId().equals(customerId)) {
      throw new UnauthorizedException("Booking does not belong to the requesting customer");
    }

    DeliveryRequest request =
        DeliveryRequest.builder()
            .bookingId(bookingId)
            .customerId(customerId)
            .warehouseId(booking.warehouseId())
            .destinationAddress(address)
            .destinationCity(city)
            .destinationCountry(country)
            .requestedDate(requestedDate)
            .status(DeliveryStatus.PENDING)
            .build();

    requestRepository.save(request);

    return request.getId();
  }

  @Transactional
  public void addItem(UUID requestId, UUID goodsItemId, BigDecimal qty) {

    DeliveryRequest request =
        requestRepository
            .findById(requestId)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", requestId));

    GoodsItemAvailabilityResponse availability = fetchGoodsItemOrThrow(goodsItemId);
    if (!availability.available()) {
      throw new BusinessRuleException(
          "Goods item is not available (status: " + availability.currentStatus() + ")");
    }

    DeliveryRequestItem item =
        DeliveryRequestItem.builder()
            .deliveryRequest(request)
            .goodsItemId(goodsItemId)
            .requestedQty(qty)
            .pickedQty(BigDecimal.ZERO)
            .build();

    itemRepository.save(item);
  }

  // ── Owner confirms delivery request ─────────────────────────────────────

  @Transactional
  public DeliveryRequestResponse confirm(UUID id, UUID ownerId) {
    DeliveryRequest request = findByIdOrThrow(id);

    if (request.getStatus() != DeliveryStatus.PENDING) {
      throw new BusinessRuleException("Only PENDING delivery requests can be confirmed");
    }

    request.setStatus(DeliveryStatus.CONFIRMED);
    request.setConfirmedBy(ownerId);
    request.setConfirmedAt(Instant.now());
    requestRepository.save(request);

    outboxPublisher.publish(
        "DeliveryRequest",
        request.getId(),
        KafkaTopics.DELIVERY_CONFIRMED,
        new DeliveryConfirmedEvent(
            request.getId(),
            request.getBookingId(),
            request.getCustomerId(),
            request.getWarehouseId(),
            ownerId));

    return DeliveryRequestResponse.from(request);
  }

  // ── Owner assigns delivery to a specific staff member ───────────────────

  @Transactional
  public DeliveryRequestResponse assignStaff(UUID id, UUID ownerId, UUID staffId) {
    DeliveryRequest request = findByIdOrThrow(id);

    if (request.getStatus() != DeliveryStatus.CONFIRMED) {
      throw new BusinessRuleException("Only CONFIRMED delivery requests can be assigned to staff");
    }

    request.setAssignedStaffId(staffId);
    requestRepository.save(request);

    return DeliveryRequestResponse.from(request);
  }

  // ── Staff accepts the assignment and starts picking ──────────────────────

  @Transactional
  public DeliveryRequestResponse acceptByStaff(UUID id, UUID staffId) {
    DeliveryRequest request = findByIdOrThrow(id);

    if (request.getStatus() != DeliveryStatus.CONFIRMED) {
      throw new BusinessRuleException(
          "Delivery request must be in CONFIRMED status for staff to accept");
    }

    if (request.getAssignedStaffId() == null) {
      throw new BusinessRuleException(
          "Delivery request has not been assigned to any staff member yet");
    }

    if (!request.getAssignedStaffId().equals(staffId)) {
      throw new UnauthorizedException("This delivery request is assigned to a different staff");
    }

    request.setStatus(DeliveryStatus.PICKING);
    requestRepository.save(request);

    return DeliveryRequestResponse.from(request);
  }

  // ── Staff starts picking (legacy, for unassigned flow) ──────────────────

  @Transactional
  public void startPicking(UUID requestId, UUID staffId) {
    DeliveryRequest request = findByIdOrThrow(requestId);

    if (request.getStatus() != DeliveryStatus.CONFIRMED) {
      throw new BusinessRuleException(
          "Delivery request must be in CONFIRMED status to start picking");
    }

    if (request.getAssignedStaffId() != null && !request.getAssignedStaffId().equals(staffId)) {
      throw new UnauthorizedException("This delivery request is assigned to a different staff");
    }

    request.setStatus(DeliveryStatus.PICKING);
    requestRepository.save(request);
  }

  // ── Customer acknowledges receipt of delivery ───────────────────────────

  @Transactional
  public DeliveryRequestResponse acknowledge(UUID id, UUID customerId) {
    DeliveryRequest request = findByIdOrThrow(id);

    if (!request.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("DeliveryRequest", id);
    }

    if (request.getStatus() != DeliveryStatus.DELIVERED) {
      throw new BusinessRuleException(
          "Only DELIVERED requests can be acknowledged by the customer");
    }

    request.setStatus(DeliveryStatus.ACKNOWLEDGED);
    request.setAcknowledgedAt(Instant.now());
    requestRepository.save(request);

    outboxPublisher.publish(
        "DeliveryRequest",
        request.getId(),
        KafkaTopics.DELIVERY_ACKNOWLEDGED,
        new DeliveryAcknowledgedEvent(
            request.getId(), request.getBookingId(), request.getCustomerId()));

    return DeliveryRequestResponse.from(request);
  }

  // ── Pick item (legacy direct pick) ──────────────────────────────────────

  @Transactional
  public void pickItem(UUID itemId, UUID staffId, BigDecimal pickedQty) {
    DeliveryRequestItem item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequestItem", itemId));

    item.setPickedQty(pickedQty);
    item.setPickedBy(staffId);

    itemRepository.save(item);
  }

  // ── Queries ─────────────────────────────────────────────────────────────

  @Transactional(readOnly = true)
  public List<DeliveryRequestResponse> listByCustomer(UUID customerId) {
    return requestRepository.findByCustomerId(customerId).stream()
        .map(DeliveryRequestResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public DeliveryRequestResponse getByIdForCustomer(UUID id, UUID customerId) {
    DeliveryRequest request = findByIdOrThrow(id);

    if (!request.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("DeliveryRequest", id);
    }

    return DeliveryRequestResponse.from(request);
  }

  @Transactional(readOnly = true)
  public List<DeliveryRequestResponse> listByWarehouse(UUID warehouseId) {
    return requestRepository.findByWarehouseId(warehouseId).stream()
        .map(DeliveryRequestResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<DeliveryRequestResponse> listAll() {
    return requestRepository.findAll().stream().map(DeliveryRequestResponse::from).toList();
  }

  @Transactional(readOnly = true)
  public List<DeliveryRequestResponse> listAvailable() {
    return requestRepository.findByStatus(DeliveryStatus.DISPATCHED).stream()
        .map(DeliveryRequestResponse::from)
        .toList();
  }

  // ── Helpers ─────────────────────────────────────────────────────────────

  private DeliveryRequest findByIdOrThrow(UUID id) {
    return requestRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", id));
  }

  private BookingStatusResponse fetchBookingOrThrow(UUID bookingId) {
    try {
      return bookingClient.getStatus(bookingId);
    } catch (Exception ex) {
      throw new EntityNotFoundException("Booking", bookingId);
    }
  }

  private GoodsItemAvailabilityResponse fetchGoodsItemOrThrow(UUID goodsItemId) {
    try {
      return inventoryClient.getAvailableQty(goodsItemId);
    } catch (Exception ex) {
      throw new EntityNotFoundException("GoodsItem", goodsItemId);
    }
  }
}
