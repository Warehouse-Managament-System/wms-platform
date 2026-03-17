package com.wms.identity.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateWarehouseOwnerRequest(
    @Size(min = 2, max = 60) String firstName,
    @Size(min = 2, max = 60) String lastName,
    @Size(min = 2, max = 100) String companyName,
    @Size(min = 5, max = 16) String taxId,
    @Size(min = 5, max = 255) String address,
    @Size(min = 2, max = 100) String city,
    @Size(min = 2, max = 100) String country) {}
