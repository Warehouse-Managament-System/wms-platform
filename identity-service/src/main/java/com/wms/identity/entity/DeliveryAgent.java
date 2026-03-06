package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "delivery_agent_profiles")
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
      unique = true,
      foreignKey = @ForeignKey(name = "fk_delivery_agent_profiles_users_user_id"))
  private User user;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "vehicle_info", nullable = false, length = 255)
  private String vehicleInfo;
}
