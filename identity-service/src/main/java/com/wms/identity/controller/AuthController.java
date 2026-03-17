package com.wms.identity.controller;

import com.wms.identity.dto.AuthResponse;
import com.wms.identity.dto.LoginRequestDto;
import com.wms.identity.dto.RefreshTokenRequest;
import com.wms.identity.dto.RegisterCustomerRequest;
import com.wms.identity.dto.RegisterOwnerRequest;
import com.wms.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register/owner")
  public ResponseEntity<Void> registerOwner(@Valid @RequestBody RegisterOwnerRequest request) {
    authService.registerOwner(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/register/customer")
  public ResponseEntity<Void> registerCustomer(
      @Valid @RequestBody RegisterCustomerRequest request) {
    authService.registerCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDto request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request));
  }
}
