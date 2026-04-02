package com.wms.reservation.service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.wms.common.enums.BookingStatus;
import com.wms.common.enums.InvoiceStatus;
import com.wms.common.enums.PaymentStatus;
import com.wms.common.event.KafkaTopics;
import com.wms.common.event.PaymentFailedEvent;
import com.wms.common.event.PaymentSuccessEvent;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.outbox.OutboxPublisher;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.entity.Invoice;
import com.wms.reservation.entity.Payment;
import com.wms.reservation.repository.BookingRepository;
import com.wms.reservation.repository.InvoiceRepository;
import com.wms.reservation.repository.PaymentRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final InvoiceRepository invoiceRepository;
  private final BookingRepository bookingRepository;
  private final StripeService stripeService;
  private final OutboxPublisher outboxPublisher;

  @Value("${stripe.webhook-secret}")
  private String webhookSecret;

  @Transactional
  public String createCheckoutSession(UUID invoiceId, UUID customerId) {
    Invoice invoice =
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice", invoiceId));

    if (!invoice.getCustomerId().equals(customerId)) {
      throw new EntityNotFoundException("Invoice", invoiceId);
    }

    if (invoice.getStatus() != InvoiceStatus.ISSUED) {
      throw new BusinessRuleException("Only issued invoices can be paid");
    }

    Payment payment =
        Payment.builder()
            .invoice(invoice)
            .stripePaymentId("pending_" + UUID.randomUUID())
            .amount(invoice.getAmount())
            .status(PaymentStatus.PENDING)
            .build();

    paymentRepository.save(payment);

    return stripeService.createCheckoutSession(
        invoice,
        "https://wms.example.com/payment/success?invoiceId=" + invoiceId,
        "https://wms.example.com/payment/cancel?invoiceId=" + invoiceId);
  }

  @Transactional
  public void handleStripeWebhook(String payload, String sigHeader) {
    Event event = stripeService.constructEvent(payload, sigHeader, webhookSecret);

    switch (event.getType()) {
      case "checkout.session.completed" -> handleCheckoutCompleted(event);
      case "checkout.session.expired", "payment_intent.payment_failed" ->
          handlePaymentFailed(event);
      default -> log.debug("Unhandled Stripe event type: {}", event.getType());
    }
  }

  private void handleCheckoutCompleted(Event event) {
    Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

    String invoiceIdStr = session.getMetadata().get("invoiceId");
    UUID invoiceId = UUID.fromString(invoiceIdStr);

    Payment payment =
        paymentRepository
            .findByInvoiceId(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Payment for invoice", invoiceId));

    payment.setStripePaymentId(session.getPaymentIntent());
    payment.setStripeReceiptUrl(session.getUrl());
    payment.setStatus(PaymentStatus.SUCCEEDED);
    payment.setPaidAt(Instant.now());
    paymentRepository.save(payment);

    Invoice invoice = payment.getInvoice();
    invoice.setStatus(InvoiceStatus.PAID);
    invoiceRepository.save(invoice);

    if (invoice.getBooking() != null) {
      Booking booking = invoice.getBooking();
      booking.setStatus(BookingStatus.ACTIVE);
      bookingRepository.save(booking);
    }

    outboxPublisher.publish(
        "Payment",
        payment.getId(),
        KafkaTopics.PAYMENT_SUCCESS,
        new PaymentSuccessEvent(
            invoiceId, invoice.getCustomerId(), payment.getAmount(), payment.getStripePaymentId()));
  }

  private void handlePaymentFailed(Event event) {
    Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

    String invoiceIdStr = session.getMetadata().get("invoiceId");
    UUID invoiceId = UUID.fromString(invoiceIdStr);

    Payment payment =
        paymentRepository
            .findByInvoiceId(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Payment for invoice", invoiceId));

    payment.setStatus(PaymentStatus.FAILED);
    payment.setFailureReason("Payment failed or session expired");
    paymentRepository.save(payment);

    outboxPublisher.publish(
        "Payment",
        payment.getId(),
        KafkaTopics.PAYMENT_FAILED,
        new PaymentFailedEvent(
            invoiceId, payment.getInvoice().getCustomerId(), payment.getFailureReason()));
  }
}
