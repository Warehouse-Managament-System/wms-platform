package com.wms.delivery.feign.dto;

import com.wms.common.enums.BookingStatus;
import java.util.UUID;

/**
 * Mirrors the response shape of reservation-service's GET /api/v1/internal/bookings/{id}/status.
 * Defined locally so delivery-service does not depend on reservation-service classes.
 */
public record BookingStatusResponse(BookingStatus status, UUID customerId, UUID warehouseId) {}
