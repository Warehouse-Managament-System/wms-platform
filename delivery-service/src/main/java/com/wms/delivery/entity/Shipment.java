package com.wms.delivery.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.enums.ShipmentStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_request_id", nullable = false)
  private DeliveryRequest deliveryRequest;

  @Column(name = "claimed_by", nullable = false)
  private UUID claimedBy;

  @Column(name = "claimed_at", nullable = false)
  private Instant claimedAt;

  @Column(name = "scheduled_pickup_time", nullable = false)
  private Instant scheduledPickupTime;

  @Column(name = "tracking_number", nullable = false, unique = true, length = 100)
  private String trackingNumber;

  @Column(name = "estimated_delivery_date", nullable = false)
  private LocalDate estimatedDeliveryDate;

  @Column(name = "actual_delivery_date")
  private LocalDate actualDeliveryDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ShipmentStatus status;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "TEXT")
  private String notes = "";
}
