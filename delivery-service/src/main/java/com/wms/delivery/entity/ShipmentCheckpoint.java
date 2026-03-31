package com.wms.delivery.entity;

import com.wms.common.enums.ShipmentStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "shipment_checkpoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentCheckpoint {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipment_id", nullable = false)
  private Shipment shipment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ShipmentStatus status;

  @Column(nullable = false, length = 255)
  private String location;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "TEXT")
  private String note = "";

  @Column(name = "recorded_at", nullable = false)
  private Instant recordedAt;

  @PrePersist
  void onCreate() {
    if (recordedAt == null) recordedAt = Instant.now();
  }
}
