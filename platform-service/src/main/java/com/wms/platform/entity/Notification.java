package com.wms.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(nullable = false, length = 50)
  private String type;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Builder.Default
  @Column(name = "is_read", nullable = false)
  private boolean read = false;

  @Column(name = "read_at")
  private Instant readAt;

  @Column(length = 500)
  private String link;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
