package com.wms.identity.entity;

import com.wms.common.entity.BaseEntity;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(name = "uk_users_email", columnNames = "email")},
    check = {
      @CheckConstraint(
          name = "ck_users_first_name",
          constraint = "LENGTH(first_name) BETWEEN 2 AND 60"),
      @CheckConstraint(
          name = "ck_users_last_name",
          constraint = "LENGTH(last_name) BETWEEN 2 AND 60"),
      @CheckConstraint(
          name = "ck_users_email",
          constraint = "email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'"),
      @CheckConstraint(name = "ck_users_password", constraint = "LENGTH(password) >= 60")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "first_name", nullable = false, length = 60)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 60)
  private String lastName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private UserRole role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private UserStatus status;
}
