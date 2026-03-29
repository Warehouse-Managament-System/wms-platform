package com.wms.goods.dto.receipt;

import java.math.BigDecimal;

public record GoodsAvailableQtyResponse(
    BigDecimal availableQty
) {}
