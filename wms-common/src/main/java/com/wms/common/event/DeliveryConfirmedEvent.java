package com.wms.common.event;

import java.util.UUID;

public record DeliveryConfirmedEvent(
    UUID deliveryId, UUID bookingId, UUID customerId, UUID warehouseId, UUID confirmedBy) {}
