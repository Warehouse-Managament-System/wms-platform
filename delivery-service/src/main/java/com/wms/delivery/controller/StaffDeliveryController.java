package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.delivery.DeliveryRequestResponse;
import com.wms.delivery.service.AgentClaimService;
import com.wms.delivery.service.DeliveryRequestService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/deliveries")
@RequiredArgsConstructor
public class StaffDeliveryController {

  private final AgentClaimService agentClaimService;
  private final DeliveryRequestService deliveryRequestService;

  @GetMapping
  public ResponseEntity<List<DeliveryRequestResponse>> list() {
    return ResponseEntity.ok(deliveryRequestService.listAll());
  }

  @PatchMapping("/{id}/start-picking")
  public ResponseEntity<Void> startPicking(@PathVariable UUID id) {
    UUID staffId = UserContextHolder.get().userId();
    deliveryRequestService.startPicking(id, staffId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/notify-agents")
  public ResponseEntity<Void> notifyAgents(@PathVariable UUID id) {
    UUID staffId = UserContextHolder.get().userId();
    agentClaimService.notifyAgents(id, staffId);
    return ResponseEntity.ok().build();
  }
}
