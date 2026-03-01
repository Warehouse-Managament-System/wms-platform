package com.wms.common.event;

import java.time.Instant;
import java.util.UUID;

public record OutboxEventEnvelope<T>(
    String aggregateType, UUID aggregateId, String eventType, T payload, Instant createdAt) {}
