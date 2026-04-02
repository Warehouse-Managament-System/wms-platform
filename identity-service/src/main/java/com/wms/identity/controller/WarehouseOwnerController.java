package com.wms.identity.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserStatus;
import com.wms.identity.dto.request.CreateWarehouseOwnerRequest;
import com.wms.identity.dto.request.RejectWarehouseOwnerRequest;
import com.wms.identity.dto.request.UpdateWarehouseOwnerRequest;
import com.wms.identity.dto.response.WarehouseOwnerResponse;
import com.wms.identity.entity.User;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.service.WarehouseOwnerService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse-owners")
@RequiredArgsConstructor
public class WarehouseOwnerController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "companyName", "taxId", "city", "country");

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
  public ResponseEntity<PageResponse<WarehouseOwnerResponse>> search(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) UserStatus status,
      @RequestParam(required = false) Instant createdFrom,
      @RequestParam(required = false) Instant createdTo,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {

    String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";

    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(safeSortBy).ascending()
            : Sort.by(safeSortBy).descending();

    return ResponseEntity.ok(
        service.search(
            search,
            status,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
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
