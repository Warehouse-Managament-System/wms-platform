package com.wms.delivery.service;

import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.delivery.dto.delivery.DeliveryRequestItemResponse;
import com.wms.delivery.dto.delivery.PickItemRequest;
import com.wms.delivery.entity.DeliveryRequestItem;
import com.wms.delivery.repository.DeliveryRequestItemRepository;
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

  @Transactional
  public DeliveryRequestItemResponse pickItem(
      UUID deliveryRequestId, UUID agentId, PickItemRequest request) {
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
    item.setPickedBy(agentId);
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
