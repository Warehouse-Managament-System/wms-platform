package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.entity.SoftDeleteEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(
    name = "warehouse_owner_profiles",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_warehouse_owner_profiles_user_id", columnNames = "user_id"),
      @UniqueConstraint(name = "uk_warehouse_owner_profiles_tax_id", columnNames = "tax_id")
    },
    check = {
      @CheckConstraint(
          name = "ck_warehouse_owner_profiles_company_name",
          constraint = "LENGTH(company_name) BETWEEN 2 AND 100"),
      @CheckConstraint(
          name = "ck_warehouse_owner_profiles_tax_id",
          constraint = "tax_id ~* '^[A-Za-z0-9-]{5,16}$'"),
      @CheckConstraint(
          name = "ck_warehouse_owner_profiles_address",
          constraint = "LENGTH(address) BETWEEN 5 AND 255"),
      @CheckConstraint(
          name = "ck_warehouse_owner_profiles_city",
          constraint = "LENGTH(city) BETWEEN 2 AND 100"),
      @CheckConstraint(
          name = "ck_warehouse_owner_profiles_country",
          constraint = "LENGTH(country) BETWEEN 2 AND 100"),
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseOwner extends BaseEntity {

  @OneToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_user_id"))
  private User user;

  @Column(name = "company_name", nullable = false, length = 100)
  private String companyName;

  @Column(name = "tax_id", nullable = false, length = 16)
  private String taxId;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false, length = 100)
  private String city;

  @Column(nullable = false, length = 100)
  private String country;

  @OneToOne
  @JoinColumn(
      name = "approved_by",
      foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_approved_by"))
  private User approvedBy;

  @Column(name = "approved_at")
  private Instant approvedAt;

  @Column(name = "rejection_reason", length = 500)
  private String rejectionReason;

  @Column(name = "deleted_at")
  private Instant deletedAt;
}
