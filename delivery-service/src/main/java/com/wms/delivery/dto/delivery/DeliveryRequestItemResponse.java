package com.wms.delivery.dto.delivery;

import com.wms.delivery.entity.DeliveryRequestItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeliveryRequestItemResponse(
    UUID id,
    UUID deliveryRequestId,
    UUID goodsItemId,
    BigDecimal requestedQty,
    BigDecimal pickedQty,
    UUID pickedBy,
    Instant pickedAt) {
  public static DeliveryRequestItemResponse from(DeliveryRequestItem item) {
    return new DeliveryRequestItemResponse(
        item.getId(),
        item.getDeliveryRequest().getId(),
        item.getGoodsItemId(),
        item.getRequestedQty(),
        item.getPickedQty(),
        item.getPickedBy(),
        item.getPickedAt());
  }
}
