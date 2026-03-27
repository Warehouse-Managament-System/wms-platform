package com.wms.inventory.dto.room;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddRoomCategoryRequest(@NotNull UUID categoryId) {}
