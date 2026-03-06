package com.wms.identity.entity;

import com.wms.common.entity.SoftDeleteEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "warehouse_owner_profiles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouse_owner_profiles_tax_id",columnNames = "tax_id")
    },
    check = {
        @CheckConstraint(name = "ck_warehouse_owner_profiles_company_name",constraint = "LENGTH(company_name) BETWEEN 2 AND 60"),
        @CheckConstraint(name = "ck_warehouse_owner_profiles_tax_id",constraint = "LENGTH(tax_id) BETWEEN 5 AND 16"),
        @CheckConstraint(name = "ck_warehouse_owner_profiles_address",constraint = "LENGTH(address) BETWEEN 5 AND 60"),
        @CheckConstraint(name = "ck_warehouse_owner_profiles_city",constraint = "LENGTH(city) BETWEEN 2 AND 60"),
        @CheckConstraint(name = "ck_warehouse_owner_profiles_country",constraint = "LENGTH(country) BETWEEN 2 AND 60"),
    })
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

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "tax_id", nullable = false)
  private String taxId;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String country;

  @OneToOne
  @JoinColumn(
      name = "approved_by",
      foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_approved_by"))
  private User approvedBy;

  @Column(name = "approved_at")
  private Instant approvedAt;

  @Column(name = "rejection_reason")
  private String rejectionReason;
}
