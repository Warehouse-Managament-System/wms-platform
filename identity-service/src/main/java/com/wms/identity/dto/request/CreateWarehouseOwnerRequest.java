package com.wms.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWarehouseOwnerRequest(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 6) String password,
    @NotBlank @Size(min = 2, max = 60) String firstName,
    @NotBlank @Size(min = 2, max = 60) String lastName,
    @NotBlank @Size(min = 2, max = 100) String companyName,
    @NotBlank @Size(min = 5, max = 16) String taxId,
    @NotBlank @Size(min = 5, max = 255) String address,
    @NotBlank @Size(min = 2, max = 100) String city,
    @NotBlank @Size(min = 2, max = 100) String country) {}
