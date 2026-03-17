package com.wms.identity.dto.deliveryagent;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateDeliveryAgentRequest(
    @NotNull(message = "User ID is required") UUID userId,
    @NotNull(message = "Warehouse ID is required") UUID warehouseId,
    @NotBlank(message = "Tax ID is required")
        @Pattern(
            regexp = "^[A-Za-z0-9-]{5,16}$",
            message = "Tax ID must be 5-16 alphanumeric characters or hyphens")
        String taxId,
    @NotBlank(message = "Vehicle info is required")
        @Size(min = 5, max = 255, message = "Vehicle info must be between 5 and 255 characters")
        String vehicleInfo) {}
