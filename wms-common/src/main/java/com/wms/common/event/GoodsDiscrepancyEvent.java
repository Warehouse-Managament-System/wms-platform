package com.wms.common.event;

import java.util.UUID;

public record GoodsDiscrepancyEvent(UUID importId, UUID warehouseId, String discrepancyDetails) {}
