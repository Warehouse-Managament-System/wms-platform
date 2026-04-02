package com.wms.reservation.dto;

import com.wms.common.enums.BookingType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingRequest(
    @NotNull BookingType bookingType,
    @NotNull UUID warehouseId,
    UUID roomId,
    UUID zoneId,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate) {}
