package com.wms.warehouse.entity;

import com.wms.common.entity.SoftDeleteEntity;
import com.wms.common.enums.RoomStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room extends SoftDeleteEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "zone_id", nullable = false)
  private Zone zone;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 255)
  private String description;

  @Column(name = "total_surface_area", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalSurfaceArea;

  @Column(name = "price_per_sqm_daily", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerSqmDaily;

  @Column(name = "price_per_sqm_weekly", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerSqmWeekly;

  @Column(name = "price_per_sqm_monthly", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerSqmMonthly;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RoomStatus status;
}
