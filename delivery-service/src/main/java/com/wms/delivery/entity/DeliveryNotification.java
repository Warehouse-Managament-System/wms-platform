package com.wms.delivery.entity;

import com.wms.common.enums.DeliveryNotificationStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "delivery_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryNotification {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_request_id", nullable = false)
  private DeliveryRequest deliveryRequest;

  @Column(name = "notified_by", nullable = false)
  private UUID notifiedBy;

  @Column(name = "notified_at", nullable = false)
  private Instant notifiedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DeliveryNotificationStatus status;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @PrePersist
  void onCreate() {
    if (notifiedAt == null) notifiedAt = Instant.now();
  }
}
