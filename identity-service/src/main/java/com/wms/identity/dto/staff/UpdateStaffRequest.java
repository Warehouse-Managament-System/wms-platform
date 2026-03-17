package com.wms.identity.dto.staff;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record UpdateStaffRequest(
    UUID warehouseId,
    @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
        String position) {}
