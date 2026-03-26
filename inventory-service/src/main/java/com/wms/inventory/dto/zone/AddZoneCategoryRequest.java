package com.wms.inventory.dto.zone;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddZoneCategoryRequest(
    @NotNull(message = "Category ID is required") UUID categoryId) {}
