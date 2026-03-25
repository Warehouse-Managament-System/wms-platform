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
    },
    check = {
      @CheckConstraint(
          name = "ck_customer_profiles_company_name",
          constraint = "LENGTH(company_name) BETWEEN 2 AND 100"),
      @CheckConstraint(
          name = "ck_customer_profiles_tax_id",
          constraint = "tax_id ~* '^[A-Za-z0-9-]{5,16}$'"),
      @CheckConstraint(
          name = "ck_customer_profiles_contact_person_name",
          constraint = "LENGTH(contact_person_name) BETWEEN 2 AND 60"),
      @CheckConstraint(
          name = "ck_customer_profiles_address",
          constraint = "LENGTH(address) BETWEEN 5 AND 255"),
      @CheckConstraint(
          name = "ck_customer_profiles_city",
          constraint = "LENGTH(city) BETWEEN 2 AND 100"),
      @CheckConstraint(
          name = "ck_customer_profiles_country",
          constraint = "LENGTH(country) BETWEEN 2 AND 100")
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

  @Column(name = "contact_person_name", nullable = false, length = 60)
  private String contactPersonName;
}
