package com.wms.delivery.service;

import com.wms.common.enums.DeliveryStatus;
import com.wms.delivery.entity.*;
import com.wms.delivery.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
            .orElseThrow(() -> new RuntimeException("Request not found"));

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
        itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));

    item.setPickedQty(pickedQty);
    item.setPickedBy(staffId);

    itemRepository.save(item);
  }
}
