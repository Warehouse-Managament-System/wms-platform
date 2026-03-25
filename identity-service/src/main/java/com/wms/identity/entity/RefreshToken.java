package com.wms.identity.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_refresh_tokens_token", columnNames = "token")
    })
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String token;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_refresh_tokens_users_user_id"))
  private User user;
}
