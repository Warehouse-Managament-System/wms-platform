package com.wms.delivery.dto.shipment;

import com.wms.common.enums.ShipmentStatus;
import com.wms.delivery.entity.Shipment;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ShipmentResponse(
    UUID id,
    UUID deliveryRequestId,
    UUID claimedBy,
    Instant claimedAt,
    Instant scheduledPickupTime,
    String trackingNumber,
    LocalDate estimatedDeliveryDate,
    LocalDate actualDeliveryDate,
    ShipmentStatus status,
    String notes,
    Instant createdAt,
    Instant updatedAt) {
  public static ShipmentResponse from(Shipment s) {
    return new ShipmentResponse(
        s.getId(),
        s.getDeliveryRequest().getId(),
        s.getClaimedBy(),
        s.getClaimedAt(),
        s.getScheduledPickupTime(),
        s.getTrackingNumber(),
        s.getEstimatedDeliveryDate(),
        s.getActualDeliveryDate(),
        s.getStatus(),
        s.getNotes(),
        s.getCreatedAt(),
        s.getUpdatedAt());
  }
}
