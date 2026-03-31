package com.wms.delivery.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "delivery_request_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequestItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_request_id", nullable = false)
  private DeliveryRequest deliveryRequest;

  @Column(name = "goods_item_id", nullable = false)
  private UUID goodsItemId;

  @Column(name = "requested_qty", nullable = false, precision = 10, scale = 2)
  private BigDecimal requestedQty;

  @Builder.Default
  @Column(name = "picked_qty", nullable = false, precision = 10, scale = 2)
  private BigDecimal pickedQty = BigDecimal.ZERO;

  @Column(name = "picked_by")
  private UUID pickedBy;

  @Column(name = "picked_at")
  private Instant pickedAt;
}
