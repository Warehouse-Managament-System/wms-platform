package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.delivery.DeliveryRequestResponse;
import com.wms.delivery.dto.shipment.ShipmentResponse;
import com.wms.delivery.service.AgentClaimService;
import com.wms.delivery.service.DeliveryRequestService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent/deliveries")
@RequiredArgsConstructor
public class AgentDeliveryController {

  private final AgentClaimService agentClaimService;
  private final DeliveryRequestService deliveryRequestService;

  @GetMapping
  public ResponseEntity<List<DeliveryRequestResponse>> listAvailable() {
    return ResponseEntity.ok(deliveryRequestService.listAvailable());
  }

  @PatchMapping("/{id}/claim")
  public ResponseEntity<ShipmentResponse> claim(@PathVariable UUID id) {
    UUID agentId = UserContextHolder.get().userId();
    return ResponseEntity.ok(agentClaimService.claim(id, agentId));
  }
}
