package com.wms.identity.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Entity
@Table(name = "delivery_agent_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgent {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true,foreignKey = @ForeignKey(name="fk_delivery_agent_profiles_users_user_id"))
  private User user;

  @JoinColumn(name = "warehouse_id", nullable = false,foreignKey = @ForeignKey(name = "fk_delivery_agent_profiles_warehouses_warehouse_id"))
  private UUID warehouse;

  @Column(name = "vehicle_info", nullable = false, length = 255)
  private String vehicleInfo;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
