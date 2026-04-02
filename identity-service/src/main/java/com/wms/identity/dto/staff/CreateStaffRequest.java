package com.wms.identity.dto.staff;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateStaffRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @NotBlank @Size(min = 2, max = 60) String firstName,
    @NotBlank @Size(min = 2, max = 60) String lastName,
    @NotNull UUID warehouseId,
    @NotBlank @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
        String position) {}
