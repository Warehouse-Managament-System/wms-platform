package com.wms.delivery.service;

import com.wms.common.enums.DeliveryStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.delivery.dto.delivery.DeliveryRequestResponse;
import com.wms.delivery.entity.*;
import com.wms.delivery.repository.*;
import java.math.BigDecimal;
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

  @Transactional
  public UUID createRequest(
      UUID bookingId,
      UUID customerId,
      String address,
      String city,
      String country,
      LocalDate requestedDate) {

    DeliveryRequest request =
        DeliveryRequest.builder()
            .bookingId(bookingId)
            .customerId(customerId)
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

    DeliveryRequestItem item =
        DeliveryRequestItem.builder()
            .deliveryRequest(request)
            .goodsItemId(goodsItemId)
            .requestedQty(qty)
            .pickedQty(BigDecimal.ZERO)
            .build();

    itemRepository.save(item);
  }

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

  @Transactional
  public void startPicking(UUID requestId, UUID staffId) {
    DeliveryRequest request =
        requestRepository
            .findById(requestId)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", requestId));

    if (request.getStatus() != DeliveryStatus.PENDING
        && request.getStatus() != DeliveryStatus.CONFIRMED) {
      throw new BusinessRuleException(
          "Delivery request must be in PENDING or CONFIRMED status to start picking");
    }

    request.setStatus(DeliveryStatus.PICKING);
    requestRepository.save(request);
  }

  @Transactional(readOnly = true)
  public List<DeliveryRequestResponse> listByCustomer(UUID customerId) {
    return requestRepository.findByCustomerId(customerId).stream()
        .map(DeliveryRequestResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public DeliveryRequestResponse getByIdForCustomer(UUID id, UUID customerId) {
    DeliveryRequest request =
        requestRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", id));

    if (!request.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("DeliveryRequest", id);
    }

    return DeliveryRequestResponse.from(request);
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
}
