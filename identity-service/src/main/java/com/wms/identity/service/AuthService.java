package com.wms.identity.service;

import com.wms.identity.dto.AuthResponse;
import com.wms.identity.dto.LoginRequestDto;
import com.wms.identity.dto.RefreshTokenRequest;
import com.wms.identity.dto.RegisterCustomerRequest;
import com.wms.identity.dto.RegisterOwnerRequest;
import com.wms.identity.dto.TokenResponseDto;
import com.wms.identity.entity.RefreshToken;
import com.wms.identity.entity.User;
import com.wms.identity.enums.Role;
import com.wms.identity.enums.Status;
import com.wms.identity.repository.RefreshTokenRepository;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    // OWNER REGISTER
    public void registerOwner(RegisterOwnerRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email artıq mövcuddur");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.WAREHOUSE_OWNER);
        user.setStatus(Status.PENDING);

        userRepository.save(user);
    }

    // CUSTOMER REGISTER
    public void registerCustomer(RegisterCustomerRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email artıq mövcuddur");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);
    }

    // LOGIN
    public AuthResponse login(LoginRequestDto request) {

        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken =
            refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
            accessToken,
            refreshToken.getToken()
        );
    }

    // REFRESH TOKEN
    public TokenResponseDto refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken =
            refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                    new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        UserDetails userDetails =
            org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String newAccessToken = jwtService.generateToken(userDetails);

        return new TokenResponseDto(
            newAccessToken,
            refreshToken.getToken()
        );
    }

}
