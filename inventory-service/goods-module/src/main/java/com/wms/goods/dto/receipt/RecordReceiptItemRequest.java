package com.wms.goods.dto.receipt;

import com.wms.common.enums.ReceiptCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordReceiptItemRequest(
    @NotNull UUID goodsItemId,
    @NotNull @DecimalMin("0") BigDecimal expectedQty,
    @NotNull @DecimalMin("0") BigDecimal receivedQty,
    @NotNull ReceiptCondition condition,
    String notes
) {}
