package com.wms.warehouse.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.enums.WarehouseStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false, length = 100)
  private String city;

  @Column(nullable = false, length = 100)
  private String country;

  @Column(nullable = false, precision = 9, scale = 6)
  private BigDecimal latitude;

  @Column(nullable = false, precision = 9, scale = 6)
  private BigDecimal longitude;

  @Column(name = "total_surface_area", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalSurfaceArea;

  @Builder.Default
  @Column(name = "discount_percentage", nullable = false)
  private int discountPercentage = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WarehouseStatus status;
}
