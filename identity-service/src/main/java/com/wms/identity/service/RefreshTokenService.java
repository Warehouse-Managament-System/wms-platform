package com.wms.identity.service;

import com.wms.identity.entity.RefreshToken;
import com.wms.identity.entity.User;
import com.wms.identity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken createRefreshToken(User user) {

        RefreshToken token = new RefreshToken();

        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
            Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return repository.save(token);
    }
}
