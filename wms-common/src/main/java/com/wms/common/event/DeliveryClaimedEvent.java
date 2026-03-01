package com.wms.common.event;

import java.util.UUID;

public record DeliveryClaimedEvent(UUID deliveryId, UUID driverId) {}
