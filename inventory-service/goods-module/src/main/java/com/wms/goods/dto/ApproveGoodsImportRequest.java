package com.wms.goods.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ApproveGoodsImportRequest(
    @NotNull Instant arrivalDeadline
) {}
