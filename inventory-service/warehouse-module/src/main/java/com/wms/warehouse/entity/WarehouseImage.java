package com.wms.warehouse.entity;

import com.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseImage extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private Warehouse warehouse;

  @Column(nullable = false, length = 500)
  private String url;

  @Builder.Default
  @Column(name = "is_primary", nullable = false)
  private Boolean isPrimary = false;
}
