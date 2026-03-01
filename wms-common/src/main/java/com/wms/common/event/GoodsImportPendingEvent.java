package com.wms.common.event;

import java.util.UUID;

public record GoodsImportPendingEvent(
    UUID importId, UUID warehouseId, UUID clientId, int itemCount) {}
