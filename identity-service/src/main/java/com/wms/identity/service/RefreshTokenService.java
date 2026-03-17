package com.wms.identity.service;

import com.wms.identity.entity.RefreshToken;
import com.wms.identity.entity.User;
import com.wms.identity.repository.RefreshTokenRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository repository;

  @Value("${jwt.refresh-exp}")
  private long refreshExpMs;

  public RefreshToken createRefreshToken(User user) {
    RefreshToken token = new RefreshToken();
    token.setUser(user);
    token.setToken(UUID.randomUUID().toString());
    token.setExpiryDate(Instant.now().plus(refreshExpMs, ChronoUnit.MILLIS));
    return repository.save(token);
  }
}
