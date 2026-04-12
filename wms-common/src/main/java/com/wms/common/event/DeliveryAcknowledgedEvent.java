package com.wms.common.event;

import java.util.UUID;

public record DeliveryAcknowledgedEvent(UUID deliveryId, UUID bookingId, UUID customerId) {}
