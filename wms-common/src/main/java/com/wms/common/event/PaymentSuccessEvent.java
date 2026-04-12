package com.wms.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentSuccessEvent(
    UUID invoiceId, UUID customerId, BigDecimal amount, String stripePaymentId) {}
