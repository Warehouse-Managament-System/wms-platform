package com.wms.identity.dto.customer;

import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
    @Size(min = 2, max = 100) String companyName,
    @Size(min = 5, max = 16) String taxId,
    @Size(min = 2, max = 60) String contactPersonName) {}
