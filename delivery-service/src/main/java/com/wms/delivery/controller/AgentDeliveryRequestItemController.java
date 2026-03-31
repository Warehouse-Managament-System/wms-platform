package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.delivery.DeliveryRequestItemResponse;
import com.wms.delivery.dto.delivery.PickItemRequest;
import com.wms.delivery.service.DeliveryRequestItemService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/delivery-requests")
@RequiredArgsConstructor
public class AgentDeliveryRequestItemController {

  private final DeliveryRequestItemService itemService;

  @PostMapping("/{id}/items/pick")
  public ResponseEntity<DeliveryRequestItemResponse> pickItem(
      @PathVariable("id") UUID deliveryRequestId, @Valid @RequestBody PickItemRequest request) {
    UUID staffId = UserContextHolder.get().userId();
    return ResponseEntity.ok(itemService.pickItem(deliveryRequestId, staffId, request));
  }

  @GetMapping("/{id}/items")
  public ResponseEntity<List<DeliveryRequestItemResponse>> listItems(
      @PathVariable("id") UUID deliveryRequestId) {
    return ResponseEntity.ok(itemService.listItems(deliveryRequestId));
  }
}
