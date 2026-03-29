package com.wms.goods.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectGoodsImportRequest(
    @NotBlank String reason
) {}
