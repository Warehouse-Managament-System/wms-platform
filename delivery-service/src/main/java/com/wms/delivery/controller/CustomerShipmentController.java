package com.wms.delivery.controller;

import com.wms.delivery.dto.shipment.ShipmentTrackingResponse;
import com.wms.delivery.entity.ShipmentCheckpoint;
import com.wms.delivery.service.ShipmentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/shipments")
@RequiredArgsConstructor
public class CustomerShipmentController {

  private final ShipmentService shipmentService;

  @GetMapping("/{id}/track")
  public ResponseEntity<ShipmentTrackingResponse> trackShipment(
      @PathVariable("id") UUID shipmentId) {
    ShipmentTrackingResponse response = shipmentService.track(shipmentId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{shipmentId}/latest-checkpoint")
  public ShipmentCheckpoint getLatestCheckpoint(@PathVariable UUID shipmentId) {
    return shipmentService.getLatestCheckpoint(shipmentId);
  }

  @GetMapping("/{shipmentId}/is-delivered")
  public boolean isDelivered(@PathVariable UUID shipmentId) {
    return shipmentService.isDelivered(shipmentId);
  }
}
