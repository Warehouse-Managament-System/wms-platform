package com.wms.delivery.dto.shipment;

import com.wms.common.enums.ShipmentStatus;
import com.wms.delivery.entity.ShipmentCheckpoint;
import java.time.Instant;
import java.util.UUID;

public record ShipmentCheckpointResponse(
    UUID id, ShipmentStatus status, String location, String note, Instant recordedAt) {
  public static ShipmentCheckpointResponse from(ShipmentCheckpoint cp) {
    return new ShipmentCheckpointResponse(
        cp.getId() == null ? null : cp.getId(),
        cp.getStatus(),
        cp.getLocation(),
        cp.getNote(),
        cp.getRecordedAt().atZone(java.time.ZoneId.systemDefault()).toInstant());
  }
}
