package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "delivery_agent_profiles",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_delivery_agent_profiles_user_id", columnNames = "user_id"),
      @UniqueConstraint(name = "uk_delivery_agent_profiles_tax_id", columnNames = "tax_id"),
    },
    check = {
      @CheckConstraint(
          name = "ck_delivery_agent_profiles_tax_id",
          constraint = "tax_id ~* '^[A-Za-z0-9-]{5,16}$'"),
      @CheckConstraint(
          name = "ck_delivery_agent_profiles_vehicle_info",
          constraint = "LENGTH(vehicle_info) BETWEEN 5 AND 255"),
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgent extends BaseEntity {

  @OneToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_delivery_agent_profiles_users_user_id"))
  private User user;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "tax_id", nullable = false, length = 16)
  private String taxId;

  @Column(name = "vehicle_info", nullable = false)
  private String vehicleInfo;
}
