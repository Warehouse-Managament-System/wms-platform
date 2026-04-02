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
    String destinationAddress,
    String destinationCity,
    String destinationCountry,
    LocalDate requestedDate,
    DeliveryStatus status,
    Instant createdAt,
    Instant updatedAt) {
  public static DeliveryRequestResponse from(DeliveryRequest dr) {
    return new DeliveryRequestResponse(
        dr.getId(),
        dr.getBookingId(),
        dr.getCustomerId(),
        dr.getDestinationAddress(),
        dr.getDestinationCity(),
        dr.getDestinationCountry(),
        dr.getRequestedDate(),
        dr.getStatus(),
        dr.getCreatedAt(),
        dr.getUpdatedAt());
  }
}
