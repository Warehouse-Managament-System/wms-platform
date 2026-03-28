package com.wms.delivery.dto.delivery;

import java.util.UUID;

public record DeliveryRequestItemResponse(
    UUID id,
    UUID deliveryRequestId,
    UUID goodsItemId,
    Integer requestedQty,
    Integer pickedQty,
    UUID pickedBy) {
  public static DeliveryRequestItemResponse from(com.wms.delivery.entity.DeliveryRequestItem item) {
    return new DeliveryRequestItemResponse(
        item.getId(),
        item.getDeliveryRequestId(),
        item.getGoodsItemId(),
        item.getRequestedQty(),
        item.getPickedQty(),
        item.getPickedBy());
  }
}
