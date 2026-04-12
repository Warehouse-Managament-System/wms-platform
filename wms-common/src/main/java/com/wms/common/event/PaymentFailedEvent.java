package com.wms.common.event;

import java.util.UUID;

public record PaymentFailedEvent(UUID invoiceId, UUID customerId, String failureReason) {}
