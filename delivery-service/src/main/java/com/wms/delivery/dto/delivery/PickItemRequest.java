package com.wms.delivery.dto.delivery;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PickItemRequest(@NotNull UUID goodsItemId, @NotNull @Min(1) Integer pickedQty) {}
