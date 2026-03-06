package com.wms.identity.entity;

import com.wms.common.entity.SoftDeleteEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "warehouse_owner_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseOwner extends SoftDeleteEntity {

  @OneToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_user_id"))
  private User user;

  @Column(name = "company_name", nullable = false, length = 50)
  private String companyName;

  @Column(name = "tax_id", unique = true, nullable = false, length = 50)
  private String taxId;

  @Column(nullable = false, length = 50)
  private String address;

  @Column(nullable = false, length = 30)
  private String city;

  @Column(nullable = false, length = 25)
  private String country;

  @ManyToOne
  @JoinColumn(
      name = "approved_by",
      foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_approved_by"))
  private User approvedBy;

  @Column(name = "approved_at")
  private Instant approvedAt;

  @Column(name = "rejection_reason")
  private String rejectionReason;
}
