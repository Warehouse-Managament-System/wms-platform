package com.wms.reservation.entity;

import com.wms.common.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id", nullable = false)
  private Invoice invoice;

  @Column(name = "stripe_payment_id", nullable = false, unique = true)
  private String stripePaymentId;

  @Builder.Default
  @Column(name = "stripe_receipt_url", nullable = false)
  private String stripeReceiptUrl = "";

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Builder.Default
  @Column(name = "failure_reason", nullable = false)
  private String failureReason = "";

  @Column(name = "paid_at")
  private Instant paidAt;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }
}
