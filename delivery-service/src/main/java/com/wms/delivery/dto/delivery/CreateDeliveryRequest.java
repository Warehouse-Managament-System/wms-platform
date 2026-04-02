package com.wms.delivery.dto.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateDeliveryRequest(
    @NotNull UUID bookingId,
    @NotBlank String address,
    @NotBlank String city,
    @NotBlank String country,
    @NotNull LocalDate requestedDate) {}
