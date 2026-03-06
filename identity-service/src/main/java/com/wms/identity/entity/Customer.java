package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "customer_profiles",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_customer_profiles_tax_id", columnNames = "tax_id"),
      @UniqueConstraint(name = "uk_customer_profiles_user_id", columnNames = "user_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

  @OneToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_customer_profiles_users_user_id"))
  private User user;

  @Column(name = "company_name")
  private String companyName;

  @Column(name = "tax_id", unique = true)
  private String taxId;

  @Column(name = "contact_person_name")
  private String contactPersonName;
}
