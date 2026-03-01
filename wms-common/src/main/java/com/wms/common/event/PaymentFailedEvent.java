package com.wms.common.event;

import java.util.UUID;

public record PaymentFailedEvent(UUID invoiceId, UUID clientId, String failureReason) {}
