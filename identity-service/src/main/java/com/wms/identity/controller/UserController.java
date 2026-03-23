package com.wms.identity.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.dto.user.UpdateUserProfileRequest;
import com.wms.identity.dto.user.UserResponse;
import com.wms.identity.service.UserService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "email", "firstName", "lastName", "role", "status");

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails principal) {
    return ResponseEntity.ok(userService.getUserByEmail(principal.getUsername()));
  }

  @PatchMapping("/me")
  public ResponseEntity<UserResponse> updateMe(
      @AuthenticationPrincipal UserDetails principal,
      @Valid @RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity.ok(userService.updateProfile(principal.getUsername(), request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping
  public ResponseEntity<PageResponse<UserResponse>> search(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) UserRole role,
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
        userService.search(
            search,
            role,
            status,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
