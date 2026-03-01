package com.wms.common.event;

import java.util.UUID;

public record GoodsApprovedEvent(UUID importId, UUID warehouseId, UUID approvedBy) {}
