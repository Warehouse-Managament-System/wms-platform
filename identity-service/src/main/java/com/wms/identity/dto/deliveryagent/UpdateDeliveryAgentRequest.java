package com.wms.identity.dto.deliveryagent;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record UpdateDeliveryAgentRequest(
    UUID warehouseId,
    @Pattern(
            regexp = "^[A-Za-z0-9-]{5,16}$",
            message = "Tax ID must be 5-16 alphanumeric characters or hyphens")
        String taxId,
    @Size(min = 5, max = 255, message = "Vehicle info must be between 5 and 255 characters")
        String vehicleInfo) {}
