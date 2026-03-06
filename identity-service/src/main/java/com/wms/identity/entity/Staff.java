package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "staff_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends BaseEntity {

  @OneToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_staff_profiles_users_user_id"))
  private User user;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "position", nullable = false, length = 100)
  private String position;
}
