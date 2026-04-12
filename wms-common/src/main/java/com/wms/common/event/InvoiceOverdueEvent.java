package com.wms.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InvoiceOverdueEvent(
    UUID invoiceId, UUID customerId, BigDecimal amount, Instant dueDate) {}
