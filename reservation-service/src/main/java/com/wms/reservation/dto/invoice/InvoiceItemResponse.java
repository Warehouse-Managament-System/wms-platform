package com.wms.reservation.dto.invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InvoiceItemResponse(
    UUID id,
    String description,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal total,
    Instant createdAt
) {
    public static InvoiceItemResponse from(InvoiceItem item) {
        return new InvoiceItemResponse(
            item.getId(),
            item.getDescription(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotal(),
            item.getCreatedAt()
        );
    }
}
