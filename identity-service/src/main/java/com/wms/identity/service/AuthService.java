package com.wms.identity.service;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.identity.config.JwtService;
import com.wms.identity.dto.AuthResponse;
import com.wms.identity.dto.LoginRequestDto;
import com.wms.identity.dto.RefreshTokenRequest;
import com.wms.identity.dto.RegisterCustomerRequest;
import com.wms.identity.dto.RegisterOwnerRequest;
import com.wms.identity.entity.Customer;
import com.wms.identity.entity.RefreshToken;
import com.wms.identity.entity.User;
import com.wms.identity.entity.WarehouseOwner;
import com.wms.identity.repository.CustomerProfileRepository;
import com.wms.identity.repository.RefreshTokenRepository;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.repository.WarehouseOwnerRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final WarehouseOwnerRepository warehouseOwnerRepository;
  private final CustomerProfileRepository customerProfileRepository;

  @Transactional
  public void registerOwner(RegisterOwnerRequest request) {
    if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
      throw new ResourceConflictException("Email already registered");
    }

    if (warehouseOwnerRepository.existsByTaxIdAndDeletedAtIsNull(request.taxId())) {
      throw new ResourceConflictException("Tax ID already in use");
    }

    User user =
        buildUser(
            request.email(),
            request.password(),
            request.firstName(),
            request.lastName(),
            UserRole.WAREHOUSE_OWNER,
            UserStatus.PENDING_APPROVAL);

    userRepository.save(user);

    WarehouseOwner owner =
        WarehouseOwner.builder()
            .user(user)
            .companyName(request.companyName())
            .taxId(request.taxId())
            .address(request.address())
            .city(request.city())
            .country(request.country())
            .build();

    warehouseOwnerRepository.save(owner);
  }

  @Transactional
  public void registerCustomer(RegisterCustomerRequest request) {
    if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
      throw new ResourceConflictException("Email already registered");
    }

    if (customerProfileRepository.existsByTaxId(request.taxId())) {
      throw new ResourceConflictException("Tax ID already in use");
    }

    User user =
        buildUser(
            request.email(),
            request.password(),
            request.firstName(),
            request.lastName(),
            UserRole.CUSTOMER,
            UserStatus.ACTIVE);

    userRepository.save(user);

    Customer customer =
        Customer.builder()
            .user(user)
            .companyName(request.companyName())
            .taxId(request.taxId())
            .address(request.address())
            .city(request.city())
            .country(request.country())
            .contactPersonName(request.contactPersonName())
            .build();

    customerProfileRepository.save(customer);
  }

  @Transactional
  public AuthResponse login(LoginRequestDto request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    assert userDetails != null;

    User user =
        userRepository
            .findByEmailAndDeletedAtIsNull(userDetails.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    refreshTokenRepository.deleteByUserId(user.getId());

    String accessToken =
        jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

    return new AuthResponse(accessToken, refreshToken.getToken());
  }

  @Transactional
  public AuthResponse refreshToken(RefreshTokenRequest request) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(request.refreshToken())
            .orElseThrow(() -> new EntityNotFoundException("Refresh token not found"));

    if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new BusinessRuleException("Refresh token has expired");
    }

    User user = refreshToken.getUser();

    String newAccessToken =
        jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

    refreshTokenRepository.delete(refreshToken);
    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

    return new AuthResponse(newAccessToken, newRefreshToken.getToken());
  }

  private User buildUser(
      String email,
      String password,
      String firstName,
      String lastName,
      UserRole role,
      UserStatus status) {
    return User.builder()
        .email(email)
        .password(passwordEncoder.encode(password))
        .firstName(firstName)
        .lastName(lastName)
        .role(role)
        .status(status)
        .build();
  }
}
