package com.wms.identity.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserStatus;
import com.wms.identity.dto.customer.CreateCustomerRequest;
import com.wms.identity.dto.customer.CustomerResponse;
import com.wms.identity.dto.customer.UpdateCustomerRequest;
import com.wms.identity.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "companyName", "taxId");

  private final CustomerService customerService;

  @PostMapping
  public ResponseEntity<CustomerResponse> create(
      @Valid @RequestBody CreateCustomerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(customerService.getById(id));
  }

  @GetMapping("/by-user/{userId}")
  public ResponseEntity<CustomerResponse> getByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(customerService.getByUserId(userId));
  }

  @GetMapping
  public ResponseEntity<PageResponse<CustomerResponse>> search(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) UserStatus status,
      @RequestParam(required = false) String taxId,
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
        customerService.search(
            search,
            status,
            taxId,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request) {
    return ResponseEntity.ok(customerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    customerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
