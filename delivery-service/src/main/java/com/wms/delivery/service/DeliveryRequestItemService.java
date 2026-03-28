package com.wms.delivery.service;

import com.wms.delivery.dto.delivery.DeliveryRequestItemResponse;
import com.wms.delivery.dto.delivery.PickItemRequest;
import com.wms.delivery.entity.DeliveryRequestItem;
import com.wms.delivery.repository.DeliveryRequestItemRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
            .orElseThrow(() -> new RuntimeException("Item not found in this delivery request"));

    int newPickedQty = item.getPickedQty() + request.pickedQty();
    if (newPickedQty > item.getRequestedQty()) {
      throw new RuntimeException("Picked quantity exceeds requested quantity");
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
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<DeliveryRequestItemResponse> listItemsByGoodsItem(UUID goodsItemId) {
    return itemRepository.findByGoodsItemId(goodsItemId).stream()
        .map(DeliveryRequestItemResponse::from)
        .collect(Collectors.toList());
  }
}
