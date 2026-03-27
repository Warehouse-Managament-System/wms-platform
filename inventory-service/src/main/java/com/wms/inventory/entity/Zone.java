package com.wms.inventory.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Zone extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  Warehouse warehouse;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 255)
  String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "temperature_type", nullable = false, length = 20)
  TemperatureType temperatureType;

  @Column(name = "total_surface_area", nullable = false, precision = 10, scale = 2)
  BigDecimal totalSurfaceArea;

  @Builder.Default
  @Column(name = "discount_percentage", nullable = false)
  int discountPercentage = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  ZoneStatus status;
}
