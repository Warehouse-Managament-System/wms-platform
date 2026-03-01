package com.wms.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceGeneratedEvent(
    UUID invoiceId, UUID bookingId, UUID clientId, BigDecimal amount, String currency) {}
