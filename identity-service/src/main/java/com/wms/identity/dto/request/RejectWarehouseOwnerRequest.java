package com.wms.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectWarehouseOwnerRequest(
    @NotBlank(message = "Reason is required") @Size(max = 500) String reason) {}
