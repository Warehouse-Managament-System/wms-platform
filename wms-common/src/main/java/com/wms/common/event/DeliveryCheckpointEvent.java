package com.wms.common.event;

import java.util.UUID;

public record DeliveryCheckpointEvent(
    UUID shipmentId, String status, String location, String trackingNumber) {}
