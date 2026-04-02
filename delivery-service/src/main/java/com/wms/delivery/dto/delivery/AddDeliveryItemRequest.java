package com.wms.delivery.dto.delivery;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AddDeliveryItemRequest(
    @NotNull UUID goodsItemId, @NotNull @DecimalMin("0.01") BigDecimal qty) {}
