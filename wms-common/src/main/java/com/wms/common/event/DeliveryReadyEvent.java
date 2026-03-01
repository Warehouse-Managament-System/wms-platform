package com.wms.common.event;

import java.util.UUID;

public record DeliveryReadyEvent(UUID deliveryId, UUID bookingId, UUID warehouseId) {}
