package com.wms.reservation.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.InvoiceStatus;
import com.wms.common.enums.InvoiceType;
import com.wms.common.event.InvoiceGeneratedEvent;
import com.wms.common.event.InvoiceOverdueEvent;
import com.wms.common.event.KafkaTopics;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.reservation.dto.invoice.InvoiceResponse;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.entity.Invoice;
import com.wms.reservation.entity.InvoiceItem;
import com.wms.reservation.repository.InvoiceItemRepository;
import com.wms.reservation.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        KafkaTopics.INVOICE_GENERATED,
        new InvoiceGeneratedEvent(
            invoice.getId(),
            booking.getId(),
            booking.getCustomerId(),
            invoice.getAmount(),
            invoice.getCurrency()));

    return invoice;
  }

  @Transactional(readOnly = true)
  public PageResponse<InvoiceResponse> listByCustomer(UUID customerId, Pageable pageable) {
    return PageResponse.from(
        invoiceRepository
            .findByCustomerId(customerId, pageable)
            .map(
                invoice -> {
                  List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());
                  return InvoiceResponse.from(invoice, items);
                }));
  }

  @Transactional(readOnly = true)
  public PageResponse<InvoiceResponse> listByWarehouse(UUID warehouseId, Pageable pageable) {
    return PageResponse.from(
        invoiceRepository
            .findByWarehouseId(warehouseId, pageable)
            .map(
                invoice -> {
                  List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());
                  return InvoiceResponse.from(invoice, items);
                }));
  }

  @Transactional(readOnly = true)
  public InvoiceResponse getByIdForCustomer(UUID id, UUID customerId) {
    Invoice invoice =
        invoiceRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invoice", id));

    if (!invoice.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("Invoice", id);
    }

    List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(id);
    return InvoiceResponse.from(invoice, items);
  }

  @Transactional(readOnly = true)
  public InvoiceResponse getByIdForOwner(UUID id) {
    Invoice invoice =
        invoiceRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invoice", id));

    List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(id);
    return InvoiceResponse.from(invoice, items);
  }

  @Transactional(readOnly = true)
  public Invoice getById(UUID id) {
    return invoiceRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Invoice", id));
  }

  @Scheduled(cron = "0 0 4 * * *")
  @Transactional
  public void checkOverdueInvoices() {
    LocalDate today = LocalDate.now();
    List<Invoice> overdueInvoices =
        invoiceRepository.findByDueDateBeforeAndStatus(today, InvoiceStatus.ISSUED);

    for (Invoice invoice : overdueInvoices) {
      invoice.setStatus(InvoiceStatus.OVERDUE);
      invoiceRepository.save(invoice);

      outboxPublisher.publish(
          "Invoice",
          invoice.getId(),
          KafkaTopics.INVOICE_OVERDUE,
          new InvoiceOverdueEvent(
              invoice.getId(),
              invoice.getCustomerId(),
              invoice.getAmount(),
              invoice.getDueDate().atStartOfDay().toInstant(ZoneOffset.UTC)));

      log.info("Invoice marked as overdue: {}", invoice.getId());
    }
  }
}
