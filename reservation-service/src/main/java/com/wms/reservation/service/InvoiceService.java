package com.wms.reservation.service;

import com.wms.common.enums.InvoiceStatus;
import com.wms.common.enums.InvoiceType;
import com.wms.common.event.InvoiceGeneratedEvent;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.entity.Invoice;
import com.wms.reservation.entity.InvoiceItem;
import com.wms.reservation.repository.InvoiceItemRepository;
import com.wms.reservation.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final InvoiceItemRepository invoiceItemRepository;
  private final OutboxPublisher outboxPublisher;

  @Transactional
  public Invoice generateBookingInvoice(Booking booking) {
    Invoice invoice =
        Invoice.builder()
            .customerId(booking.getCustomerId())
            .warehouseId(booking.getWarehouseId())
            .booking(booking)
            .invoiceType(InvoiceType.BOOKING)
            .amount(booking.getTotalPrice())
            .currency("USD")
            .status(InvoiceStatus.ISSUED)
            .dueDate(LocalDate.now().plusDays(7))
            .build();

    invoice = invoiceRepository.save(invoice);

    InvoiceItem item =
        InvoiceItem.builder()
            .invoice(invoice)
            .description(
                "Warehouse booking: "
                    + booking.getBookingType()
                    + " from "
                    + booking.getStartDate()
                    + " to "
                    + booking.getEndDate())
            .quantity(BigDecimal.ONE)
            .unitPrice(booking.getTotalPrice())
            .total(booking.getTotalPrice())
            .build();

    invoiceItemRepository.save(item);

    outboxPublisher.publish(
        "Invoice",
        invoice.getId(),
        "invoice.generated",
        new InvoiceGeneratedEvent(
            invoice.getId(),
            booking.getId(),
            booking.getCustomerId(),
            invoice.getAmount(),
            invoice.getCurrency()));

    return invoice;
  }
}
