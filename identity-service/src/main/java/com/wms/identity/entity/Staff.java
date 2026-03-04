package com.wms.identity.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Entity
@Table(name = "staff_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_staff_profiles_users_user_id"))
  private User user;

  @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_staff_profiles_warehouses_warehouse_id") )
  private UUID warehouse;

  @Column(name = "position", nullable = false, length = 100)
  private String position;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
