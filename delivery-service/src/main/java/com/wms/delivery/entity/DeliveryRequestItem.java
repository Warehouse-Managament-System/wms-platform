package com.wms.delivery.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "delivery_request_items",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"delivery_request_id", "goods_item_id"})})
public class DeliveryRequestItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "delivery_request_id", nullable = false)
  private UUID deliveryRequestId;

  @Column(name = "goods_item_id", nullable = false)
  private UUID goodsItemId; // Reference to goods item

  @Column(name = "requested_qty", nullable = false)
  private Integer requestedQty;

  @Column(name = "picked_qty", nullable = false)
  private Integer pickedQty = 0;

  @Column(name = "picked_by")
  private UUID pickedBy; // Agent/user who picked

  @Column(name = "picked_at")
  private Instant pickedAt;
}
