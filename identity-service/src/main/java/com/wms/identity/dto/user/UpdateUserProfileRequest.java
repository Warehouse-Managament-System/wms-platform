package com.wms.identity.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
    @Size(min = 2, max = 60) String firstName, @Size(min = 2, max = 60) String lastName) {}
