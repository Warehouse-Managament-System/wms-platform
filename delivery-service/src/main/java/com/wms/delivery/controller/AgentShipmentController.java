package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.shipment.AddCheckpointRequest;
import com.wms.delivery.dto.shipment.ShipmentCheckpointResponse;
import com.wms.delivery.dto.shipment.ShipmentTrackingResponse;
import com.wms.delivery.service.ShipmentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent/shipments")
@RequiredArgsConstructor
public class AgentShipmentController {

  private final ShipmentService shipmentService;

  @PostMapping("/{id}/checkpoints")
  public ResponseEntity<ShipmentCheckpointResponse> addCheckpoint(
      @PathVariable("id") UUID shipmentId, @Valid @RequestBody AddCheckpointRequest request) {
    UUID agentId = UserContextHolder.get().userId();
    return ResponseEntity.ok(shipmentService.addCheckpoint(shipmentId, agentId, request));
  }

  @GetMapping("/{id}/track")
  public ResponseEntity<ShipmentTrackingResponse> track(@PathVariable("id") UUID shipmentId) {
    return ResponseEntity.ok(shipmentService.track(shipmentId));
  }
}
