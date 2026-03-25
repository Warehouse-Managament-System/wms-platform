package com.wms.identity.dto.customer;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
    @Size(min = 2, max = 100) String companyName,
    @Pattern(regexp = "^[A-Za-z0-9-]{5,16}$", message = "Tax ID format is invalid") String taxId,
    @Size(min = 5, max = 255) String address,
    @Size(min = 2, max = 100) String city,
    @Size(min = 2, max = 100) String country,
    @Size(min = 2, max = 60) String contactPersonName) {}
