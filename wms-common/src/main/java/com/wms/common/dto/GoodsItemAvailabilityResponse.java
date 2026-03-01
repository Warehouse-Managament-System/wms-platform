package com.wms.common.dto;

import java.util.UUID;

public record GoodsItemAvailabilityResponse(
    UUID goodsItemId, boolean available, String currentStatus) {}
