package com.wms.identity.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCustomerRequest(
    @NotNull(message = "User ID is required") UUID userId,
    @NotBlank(message = "Company name is required") @Size(min = 2, max = 100) String companyName,
    @NotBlank(message = "Tax ID is required") @Size(min = 5, max = 16) String taxId,
    @NotBlank(message = "Contact person name is required") @Size(min = 2, max = 60)
        String contactPersonName) {}
