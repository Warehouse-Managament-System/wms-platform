package com.wms.warehouse.dto.warehouse;

import jakarta.validation.constraints.NotBlank;

public record AddWarehouseImageRequest(@NotBlank String url, boolean isPrimary) {}
