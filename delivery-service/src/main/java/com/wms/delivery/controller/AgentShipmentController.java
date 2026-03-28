package com.wms.delivery.controller;

import com.wms.delivery.dto.shipment.AddCheckpointRequest;
import com.wms.delivery.dto.shipment.ShipmentCheckpointResponse;
import com.wms.delivery.entity.ShipmentCheckpoint;
import com.wms.delivery.service.ShipmentService;
import java.util.List;
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
      @PathVariable("id") UUID shipmentId, @RequestBody AddCheckpointRequest request) {
    UUID agentId = getAgentIdFromContext();

    ShipmentCheckpointResponse response =
        shipmentService.addCheckpoint(shipmentId, agentId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/checkpoints")
  public ResponseEntity<List<ShipmentCheckpointResponse>> getCheckpoints(
      @PathVariable("id") UUID shipmentId) {
    List<ShipmentCheckpointResponse> checkpoints =
        shipmentService.getCheckpointsByStatus(shipmentId, null);
    return ResponseEntity.ok(checkpoints);
  }

  private UUID getAgentIdFromContext() {
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
  }

  @GetMapping("/{shipmentId}/latest-checkpoint")
  public ShipmentCheckpoint getLatestCheckpointForAgent(@PathVariable UUID shipmentId) {
    return shipmentService.getLatestCheckpoint(shipmentId);
  }

  @GetMapping("/{shipmentId}/is-delivered")
  public boolean isDeliveredForAgent(@PathVariable UUID shipmentId) {
    return shipmentService.isDelivered(shipmentId);
  }
}
