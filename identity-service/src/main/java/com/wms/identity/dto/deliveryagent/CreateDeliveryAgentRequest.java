package com.wms.identity.dto.deliveryagent;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateDeliveryAgentRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @NotBlank @Size(min = 2, max = 60) String firstName,
    @NotBlank @Size(min = 2, max = 60) String lastName,
    @NotNull UUID warehouseId,
    @NotBlank
        @Pattern(
            regexp = "^[A-Za-z0-9-]{5,16}$",
            message = "Tax ID must be 5-16 alphanumeric characters or hyphens")
        String taxId,
    @NotBlank
        @Size(min = 5, max = 255, message = "Vehicle info must be between 5 and 255 characters")
        String vehicleInfo) {}
