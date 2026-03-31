package com.wms.reservation.dto.invoice;

import com.wms.common.enums.InvoiceStatus;
import com.wms.common.enums.InvoiceType;
import com.wms.reservation.entity.Invoice;
import com.wms.reservation.entity.InvoiceItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
    UUID id,
    UUID customerId,
    UUID warehouseId,
    UUID bookingId,
    UUID deliveryRequestId,
    InvoiceType invoiceType,
    BigDecimal amount,
    String currency,
    InvoiceStatus status,
    LocalDate dueDate,
    List<InvoiceItemResponse> items,
    Instant createdAt,
    Instant updatedAt
) {
    public static InvoiceResponse from(Invoice invoice, List<InvoiceItem> items) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getCustomerId(),
            invoice.getWarehouseId(),
            invoice.getBooking() != null ? invoice.getBooking().getId() : null,
            invoice.getDeliveryRequestId(),
            invoice.getInvoiceType(),
            invoice.getAmount(),
            invoice.getCurrency(),
            invoice.getStatus(),
            invoice.getDueDate(),
            items.stream().map(InvoiceItemResponse::from).toList(),
            invoice.getCreatedAt(),
            invoice.getUpdatedAt()
        );
    }
}
