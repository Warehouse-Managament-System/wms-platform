package com.wms.platform.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.event.KafkaTopics;
import com.wms.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlatformEventListener {

  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = KafkaTopics.OWNER_APPROVED, groupId = "platform-notifications")
  public void onOwnerApproved(String payload) {
    JsonNode node = parse(payload);
    String email = text(node, "email");
    String message = "Your warehouse owner registration has been approved. Welcome!";
    notificationService.createAndEmail(
        uuid(node, "userId"), email, "WAREHOUSE_APPROVED", "WMS — Registration Approved", message);
  }

  @KafkaListener(topics = KafkaTopics.OWNER_REJECTED, groupId = "platform-notifications")
  public void onOwnerRejected(String payload) {
    JsonNode node = parse(payload);
    String email = text(node, "email");
    String reason = text(node, "reason");
    String message = "Your warehouse owner registration has been rejected. Reason: " + reason;
    notificationService.createAndEmail(
        uuid(node, "userId"), email, "WAREHOUSE_REJECTED", "WMS — Registration Rejected", message);
  }

  @KafkaListener(topics = KafkaTopics.GOODS_IMPORT_PENDING, groupId = "platform-notifications")
  public void onGoodsImportPending(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "GOODS_IMPORT_PENDING",
        "Your goods import has been submitted and is pending approval.");
  }

  @KafkaListener(topics = KafkaTopics.GOODS_APPROVED, groupId = "platform-notifications")
  public void onGoodsApproved(String payload) {
    JsonNode node = parse(payload);
    log.info(
        "Goods approved: importId={}, warehouseId={}, approvedBy={}",
        text(node, "importId"),
        text(node, "warehouseId"),
        text(node, "approvedBy"));
    notificationService.create(
        uuid(node, "customerId"), "GOODS_APPROVED", "Your goods import has been approved.");
  }

  @KafkaListener(topics = KafkaTopics.GOODS_REJECTED, groupId = "platform-notifications")
  public void onGoodsRejected(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "GOODS_REJECTED",
        "Goods import has been rejected. Reason: " + text(node, "reason"));
  }

  @KafkaListener(topics = KafkaTopics.GOODS_DISCREPANCY, groupId = "platform-notifications")
  public void onGoodsDiscrepancy(String payload) {
    log.info("Goods discrepancy event received: {}", payload);
  }

  @KafkaListener(topics = KafkaTopics.BOOKING_CONFIRMED, groupId = "platform-notifications")
  public void onBookingConfirmed(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "BOOKING_CONFIRMED",
        "Your booking has been confirmed. Booking ID: " + text(node, "bookingId"));
  }

  @KafkaListener(topics = KafkaTopics.BOOKING_CANCELLED, groupId = "platform-notifications")
  public void onBookingCancelled(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "BOOKING_CANCELLED",
        "Your booking has been cancelled. Reason: " + text(node, "reason"));
  }

  @KafkaListener(topics = KafkaTopics.BOOKING_EXPIRY_SOON, groupId = "platform-notifications")
  public void onBookingExpirySoon(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "BOOKING_EXPIRY",
        "Your booking is expiring soon. Please review and take action.");
  }

  @KafkaListener(topics = KafkaTopics.BOOKING_EXPIRED, groupId = "platform-notifications")
  public void onBookingExpired(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"), "BOOKING_EXPIRY", "Your booking has expired.");
  }

  @KafkaListener(topics = KafkaTopics.INVOICE_GENERATED, groupId = "platform-notifications")
  public void onInvoiceGenerated(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "INVOICE_GENERATED",
        "A new invoice has been generated. Amount: "
            + text(node, "amount")
            + " "
            + text(node, "currency"));
  }

  @KafkaListener(topics = KafkaTopics.PAYMENT_SUCCESS, groupId = "platform-notifications")
  public void onPaymentSuccess(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "PAYMENT_SUCCESS",
        "Your payment has been processed successfully.");
  }

  @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "platform-notifications")
  public void onPaymentFailed(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "PAYMENT_FAILED",
        "Your payment has failed. Reason: " + text(node, "failureReason"));
  }

  @KafkaListener(topics = KafkaTopics.INVOICE_OVERDUE, groupId = "platform-notifications")
  public void onInvoiceOverdue(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "INVOICE_OVERDUE",
        "Your invoice is overdue. Please make payment as soon as possible.");
  }

  @KafkaListener(topics = KafkaTopics.DELIVERY_CONFIRMED, groupId = "platform-notifications")
  public void onDeliveryConfirmed(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "DELIVERY_CONFIRMED",
        "Your delivery request has been confirmed by the warehouse owner.");
  }

  @KafkaListener(topics = KafkaTopics.DELIVERY_READY, groupId = "platform-notifications")
  public void onDeliveryReady(String payload) {
    JsonNode node = parse(payload);
    notificationService.create(
        uuid(node, "customerId"),
        "DELIVERY_AVAILABLE",
        "Your delivery request is ready and available for pickup by agents.");
  }

  @KafkaListener(topics = KafkaTopics.DELIVERY_CLAIMED, groupId = "platform-notifications")
  public void onDeliveryClaimed(String payload) {
    JsonNode node = parse(payload);
    log.info(
        "Delivery claimed: deliveryId={}, agentId={}, trackingNumber={}",
        text(node, "deliveryId"),
        text(node, "agentId"),
        text(node, "trackingNumber"));
  }

  @KafkaListener(topics = KafkaTopics.DELIVERY_ACKNOWLEDGED, groupId = "platform-notifications")
  public void onDeliveryAcknowledged(String payload) {
    JsonNode node = parse(payload);
    log.info(
        "Delivery acknowledged by customer: deliveryId={}, customerId={}",
        text(node, "deliveryId"),
        text(node, "customerId"));
  }

  @KafkaListener(topics = KafkaTopics.DELIVERY_CHECKPOINT, groupId = "platform-notifications")
  public void onDeliveryCheckpoint(String payload) {
    JsonNode node = parse(payload);
    log.info(
        "Delivery checkpoint: shipmentId={}, status={}, location={}",
        text(node, "shipmentId"),
        text(node, "status"),
        text(node, "location"));
  }

  private JsonNode parse(String payload) {
    try {
      return objectMapper.readTree(payload);
    } catch (Exception e) {
      log.error("Failed to parse event payload: {}", payload, e);
      throw new IllegalArgumentException("Failed to parse event payload", e);
    }
  }

  private java.util.UUID uuid(JsonNode node, String field) {
    JsonNode fieldNode = node.get(field);
    if (fieldNode == null || fieldNode.isNull()) {
      throw new IllegalArgumentException(
          "Required UUID field '%s' missing from event payload".formatted(field));
    }
    return java.util.UUID.fromString(fieldNode.asText());
  }

  private String text(JsonNode node, String field) {
    JsonNode fieldNode = node.get(field);
    return fieldNode != null ? fieldNode.asText() : "";
  }
}
