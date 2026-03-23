package com.wms.identity.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCustomerRequest(
    @NotNull(message = "User ID is required") UUID userId,
    @NotBlank @Size(min = 2, max = 100) String companyName,
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9-]{5,16}$") String taxId,
    @NotBlank @Size(min = 5, max = 255) String address,
    @NotBlank @Size(min = 2, max = 100) String city,
    @NotBlank @Size(min = 2, max = 100) String country,
    @NotBlank @Size(min = 2, max = 60) String contactPersonName) {}
