package com.wms.delivery.service;

import com.wms.common.enums.DeliveryStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.delivery.dto.delivery.DeliveryRequestItemResponse;
import com.wms.delivery.dto.delivery.PickItemRequest;
import com.wms.delivery.entity.DeliveryRequest;
import com.wms.delivery.entity.DeliveryRequestItem;
import com.wms.delivery.repository.DeliveryRequestItemRepository;
import com.wms.delivery.repository.DeliveryRequestRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryRequestItemService {

  private final DeliveryRequestItemRepository itemRepository;
  private final DeliveryRequestRepository deliveryRequestRepository;

  @Transactional
  public DeliveryRequestItemResponse pickItem(
      UUID deliveryRequestId, UUID staffId, PickItemRequest request) {
    DeliveryRequest deliveryRequest =
        deliveryRequestRepository
            .findById(deliveryRequestId)
            .orElseThrow(() -> new EntityNotFoundException("DeliveryRequest", deliveryRequestId));

    if (deliveryRequest.getStatus() != DeliveryStatus.PICKING) {
      throw new BusinessRuleException("Delivery request must be in PICKING status to pick items");
    }

    DeliveryRequestItem item =
        itemRepository
            .findByDeliveryRequestIdAndGoodsItemId(deliveryRequestId, request.goodsItemId())
            .orElseThrow(
                () -> new EntityNotFoundException("DeliveryRequestItem", request.goodsItemId()));

    BigDecimal newPickedQty = item.getPickedQty().add(request.pickedQty());
    if (newPickedQty.compareTo(item.getRequestedQty()) > 0) {
      throw new BusinessRuleException("Picked quantity exceeds requested quantity");
    }

    item.setPickedQty(newPickedQty);
    item.setPickedBy(staffId);
    item.setPickedAt(Instant.now());

    itemRepository.save(item);

    return DeliveryRequestItemResponse.from(item);
  }

  @Transactional(readOnly = true)
  public List<DeliveryRequestItemResponse> listItems(UUID deliveryRequestId) {
    return itemRepository.findByDeliveryRequestId(deliveryRequestId).stream()
        .map(DeliveryRequestItemResponse::from)
        .toList();
  }
}
