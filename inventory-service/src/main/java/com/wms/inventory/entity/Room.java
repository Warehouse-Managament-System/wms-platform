package com.wms.inventory.entity;

import com.wms.common.enums.RoomStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "rooms",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_rooms_zone_name",
          columnNames = {"zone_id", "name"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "zone_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rooms_zones"))
  private Zone zone;

  @Column(nullable = false)
  private String name;

  @Column(name = "total_surface_area", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalSurfaceArea;

  @Column(name = "price_per_sqm_daily", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerSqmDaily;

  @Column(name = "price_per_sqm_weekly", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerSqmWeekly;

  @Column(name = "price_per_sqm_monthly", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerSqmMonthly;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoomStatus status; // <-- use your external enum

  @Column(name = "deleted_at")
  private Instant deletedAt;

  /** Soft delete helper */
  public void softDelete() {
    this.deletedAt = Instant.now();
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }
}
