package com.wms.identity.dto.staff;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateStaffRequest(
    @NotNull(message = "User ID is required") UUID userId,
    @NotNull(message = "Warehouse ID is required") UUID warehouseId,
    @NotBlank(message = "Position is required")
        @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
        String position) {}
