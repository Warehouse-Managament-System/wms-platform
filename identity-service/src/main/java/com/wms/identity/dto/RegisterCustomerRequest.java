package com.wms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
    @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
    @NotBlank(message = "First name is required") @Size(min = 2, max = 60) String firstName,
    @NotBlank(message = "Last name is required") @Size(min = 2, max = 60) String lastName,
    @NotBlank(message = "Company name is required") @Size(min = 2, max = 100) String companyName,
    @NotBlank(message = "Tax ID is required")
        @Pattern(regexp = "^[A-Za-z0-9-]{5,16}$", message = "Tax ID format is invalid")
        String taxId,
    @NotBlank(message = "Contact person name is required") @Size(min = 2, max = 60)
        String contactPersonName) {}
