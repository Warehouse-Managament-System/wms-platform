package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "staff_profiles",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_staff_profiles_user_id", columnNames = "user_id")
    },
    check = {
      @CheckConstraint(
          name = "ck_staff_profiles_position",
          constraint = "LENGTH(position) BETWEEN 2 AND 100")
    })
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
      foreignKey = @ForeignKey(name = "fk_staff_profiles_users_user_id"))
  private User user;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "position", nullable = false, length = 100)
  private String position;
}
