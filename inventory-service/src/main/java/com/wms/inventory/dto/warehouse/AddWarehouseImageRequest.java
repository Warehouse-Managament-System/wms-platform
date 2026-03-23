package com.wms.inventory.dto.warehouse;

import jakarta.validation.constraints.NotBlank;

public record AddWarehouseImageRequest(@NotBlank String url, boolean isPrimary) {}
