package com.wms.common.event;

import java.time.Instant;
import java.util.UUID;

public record DeliveryCheckpointEvent(
    UUID deliveryId, String location, String status, Instant timestamp) {}
