package com.wms.delivery.dto.delivery;

import com.wms.common.enums.DeliveryStatus;
import com.wms.delivery.entity.DeliveryRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DeliveryRequestResponse(
    UUID id,
    UUID bookingId,
    UUID customerId,
    UUID warehouseId,
    String destinationAddress,
    String destinationCity,
    String destinationCountry,
    LocalDate requestedDate,
    DeliveryStatus status,
    UUID confirmedBy,
    Instant confirmedAt,
    UUID assignedStaffId,
    Instant acknowledgedAt,
    Instant createdAt,
    Instant updatedAt) {
  public static DeliveryRequestResponse from(DeliveryRequest dr) {
    return new DeliveryRequestResponse(
        dr.getId(),
        dr.getBookingId(),
        dr.getCustomerId(),
        dr.getWarehouseId(),
        dr.getDestinationAddress(),
        dr.getDestinationCity(),
        dr.getDestinationCountry(),
        dr.getRequestedDate(),
        dr.getStatus(),
        dr.getConfirmedBy(),
        dr.getConfirmedAt(),
        dr.getAssignedStaffId(),
        dr.getAcknowledgedAt(),
        dr.getCreatedAt(),
        dr.getUpdatedAt());
  }
}
