package com.wms.common.event;

public final class KafkaTopics {
  private KafkaTopics() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static final String OWNER_APPROVED = "identity.owner.approved";
  public static final String OWNER_REJECTED = "identity.owner.rejected";

  public static final String GOODS_IMPORT_PENDING = "inventory.goods.import.pending";
  public static final String GOODS_APPROVED = "inventory.goods.approved";
  public static final String GOODS_REJECTED = "inventory.goods.rejected";
  public static final String GOODS_DISCREPANCY = "inventory.goods.discrepancy";

  public static final String BOOKING_CONFIRMED = "reservation.booking.confirmed";
  public static final String BOOKING_CANCELLED = "reservation.booking.cancelled";
  public static final String BOOKING_EXPIRY_SOON = "reservation.booking.expiry.soon";
  public static final String BOOKING_EXPIRED = "reservation.booking.expired";
  public static final String INVOICE_GENERATED = "reservation.invoice.generated";
  public static final String PAYMENT_SUCCESS = "reservation.payment.success";
  public static final String PAYMENT_FAILED = "reservation.payment.failed";
  public static final String INVOICE_OVERDUE = "reservation.invoice.overdue";

  public static final String DELIVERY_READY = "delivery.ready";
  public static final String DELIVERY_CLAIMED = "delivery.claimed";
  public static final String DELIVERY_CHECKPOINT = "delivery.checkpoint";
}
