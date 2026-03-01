package com.wms.common.event;

import java.util.UUID;

public record GoodsRejectedEvent(UUID importId, UUID warehouseId, String reason) {}
