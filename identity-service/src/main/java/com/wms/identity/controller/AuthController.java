package com.wms.identity.controller;

import com.wms.identity.dto.AuthResponse;
import com.wms.identity.dto.LoginRequestDto;
import com.wms.identity.dto.RefreshTokenRequest;
import com.wms.identity.dto.RegisterCustomerRequest;
import com.wms.identity.dto.RegisterOwnerRequest;
import com.wms.identity.dto.TokenResponseDto;
import com.wms.identity.service.AuthService;
import com.wms.identity.service.CustomUserDetailsService;
import com.wms.identity.config.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")

public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;



    public AuthController(AuthService authService, JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }
//    @PostMapping("/register")
//    public void register(@RequestBody RegisterRequestDto request) {
//        authService.register(request);
//    }
    @PostMapping("/register/owner")
    public void registerOwner(@Valid @RequestBody RegisterOwnerRequest request) {
        authService.registerOwner(request);
    }

    @PostMapping("/register/customer")
    public void registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        authService.registerCustomer(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequestDto request) {

        return authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequest  request) {
        TokenResponseDto tokenResponseDto = authService.refreshToken(request);
        return ResponseEntity.ok(tokenResponseDto);
    }


}
