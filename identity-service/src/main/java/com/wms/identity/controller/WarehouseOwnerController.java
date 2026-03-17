package com.wms.identity.controller;

import com.wms.identity.dto.request.CreateWarehouseOwnerRequest;
import com.wms.identity.dto.request.RejectWarehouseOwnerRequest;
import com.wms.identity.dto.request.UpdateWarehouseOwnerRequest;
import com.wms.identity.dto.response.WarehouseOwnerResponse;
import com.wms.identity.entity.User;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.service.WarehouseOwnerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse-owners")
@RequiredArgsConstructor
public class WarehouseOwnerController {

  private final WarehouseOwnerService service;
  private final UserRepository userRepository;

  @PostMapping
  public ResponseEntity<WarehouseOwnerResponse> create(
      @Valid @RequestBody CreateWarehouseOwnerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WarehouseOwnerResponse> get(@PathVariable UUID id) {
    return ResponseEntity.ok(service.get(id));
  }

  @GetMapping
  public ResponseEntity<List<WarehouseOwnerResponse>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @PostMapping("/{id}/approve")
  public ResponseEntity<WarehouseOwnerResponse> approve(
      @PathVariable UUID id, @AuthenticationPrincipal UserDetails principal) {
    UUID adminId = resolveUserId(principal);
    return ResponseEntity.ok(service.approve(id, adminId));
  }

  @PostMapping("/{id}/reject")
  public ResponseEntity<WarehouseOwnerResponse> reject(
      @PathVariable UUID id,
      @Valid @RequestBody RejectWarehouseOwnerRequest request,
      @AuthenticationPrincipal UserDetails principal) {
    UUID adminId = resolveUserId(principal);
    return ResponseEntity.ok(service.reject(id, adminId, request.reason()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<WarehouseOwnerResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateWarehouseOwnerRequest request) {
    return ResponseEntity.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  private UUID resolveUserId(UserDetails principal) {
    User user =
        userRepository
            .findByEmailAndDeletedAtIsNull(principal.getUsername())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    return user.getId();
  }
}
