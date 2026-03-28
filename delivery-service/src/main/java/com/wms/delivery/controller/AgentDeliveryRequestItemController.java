package com.wms.delivery.controller;

import com.wms.delivery.dto.delivery.DeliveryRequestItemResponse;
import com.wms.delivery.dto.delivery.PickItemRequest;
import com.wms.delivery.service.DeliveryRequestItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent/delivery-requests")
@RequiredArgsConstructor
public class AgentDeliveryRequestItemController {

  private final DeliveryRequestItemService itemService;

  @PostMapping("/{id}/items/pick")
  public ResponseEntity<DeliveryRequestItemResponse> pickItem(
      @PathVariable("id") UUID deliveryRequestId, @RequestBody PickItemRequest request) {
    UUID agentId = getAgentIdFromContext(); // TODO: real auth
    return ResponseEntity.ok(itemService.pickItem(deliveryRequestId, agentId, request));
  }

  @GetMapping("/{id}/items")
  public ResponseEntity<List<DeliveryRequestItemResponse>> listItems(
      @PathVariable("id") UUID deliveryRequestId) {
    return ResponseEntity.ok(itemService.listItems(deliveryRequestId));
  }

  private UUID getAgentIdFromContext() {
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
  }

  @GetMapping("/by-goods/{goodsItemId}")
  public List<DeliveryRequestItemResponse> getItemsByGoods(@PathVariable UUID goodsItemId) {
    return itemService.listItemsByGoodsItem(goodsItemId);
  }
}
