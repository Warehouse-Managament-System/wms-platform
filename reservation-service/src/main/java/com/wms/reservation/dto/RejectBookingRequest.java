package com.wms.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectBookingRequest(
    @NotBlank String reason
) {
}
