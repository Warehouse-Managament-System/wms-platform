package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.service.AgentClaimService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent/deliveries")
@RequiredArgsConstructor
public class AgentDeliveryController {

  private final AgentClaimService agentClaimService;

  @PatchMapping("/{id}/claim")
  public ResponseEntity<Void> claim(@PathVariable UUID id) {
    UUID agentId = UserContextHolder.get().userId();
    agentClaimService.claim(id, agentId);
    return ResponseEntity.ok().build();
  }
}
