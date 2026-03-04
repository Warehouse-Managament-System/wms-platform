package com.wms.identity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "delivery_agent_profiles")
@Getter
@Setter
public class DeliveryAgentProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @JoinColumn(name = "warehouse_id", nullable = false)
  private UUID warehouse;

  @Column(name = "vehicle_info", nullable = false, length = 255)
  private String vehicleInfo;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
