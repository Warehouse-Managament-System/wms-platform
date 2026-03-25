package com.wms.identity.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserStatus;
import com.wms.identity.dto.staff.CreateStaffRequest;
import com.wms.identity.dto.staff.StaffResponse;
import com.wms.identity.dto.staff.UpdateStaffRequest;
import com.wms.identity.service.StaffService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "position");

  private final StaffService staffService;

  @PostMapping
  public ResponseEntity<StaffResponse> create(@Valid @RequestBody CreateStaffRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(staffService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<StaffResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(staffService.getById(id));
  }

  @GetMapping("/by-user/{userId}")
  public ResponseEntity<StaffResponse> getByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(staffService.getByUserId(userId));
  }

  @GetMapping
  public ResponseEntity<PageResponse<StaffResponse>> search(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) UUID warehouseId,
      @RequestParam(required = false) UserStatus status,
      @RequestParam(required = false) String position,
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
        staffService.search(
            search,
            warehouseId,
            status,
            position,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<StaffResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateStaffRequest request) {
    return ResponseEntity.ok(staffService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    staffService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
