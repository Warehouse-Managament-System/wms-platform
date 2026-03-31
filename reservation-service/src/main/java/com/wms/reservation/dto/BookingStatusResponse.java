package com.wms.reservation.dto;

import com.wms.common.enums.BookingStatus;

import java.util.UUID;

public record BookingStatusResponse(
    BookingStatus status,
    UUID customerId,
    UUID warehouseId
) {
}
