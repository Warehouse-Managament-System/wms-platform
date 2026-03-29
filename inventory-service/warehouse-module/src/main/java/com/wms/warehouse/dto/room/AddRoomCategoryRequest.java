package com.wms.warehouse.dto.room;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddRoomCategoryRequest(@NotNull UUID categoryId) {}
