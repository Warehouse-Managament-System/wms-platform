package com.wms.goods.dto.receipt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateGoodsReceiptRequest(
    @NotNull UUID bookingId,
    @NotBlank String inboundCarrier,
    String notes
) {}
