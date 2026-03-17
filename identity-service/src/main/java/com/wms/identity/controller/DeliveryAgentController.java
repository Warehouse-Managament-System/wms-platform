package com.wms.identity.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserStatus;
import com.wms.identity.dto.deliveryagent.CreateDeliveryAgentRequest;
import com.wms.identity.dto.deliveryagent.DeliveryAgentResponse;
import com.wms.identity.dto.deliveryagent.UpdateDeliveryAgentRequest;
import com.wms.identity.service.DeliveryAgentService;
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
@RequestMapping("/api/v1/delivery-agents")
@RequiredArgsConstructor
public class DeliveryAgentController {

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "taxId", "vehicleInfo");

  private final DeliveryAgentService deliveryAgentService;

  @PostMapping
  public ResponseEntity<DeliveryAgentResponse> create(
      @Valid @RequestBody CreateDeliveryAgentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(deliveryAgentService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DeliveryAgentResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(deliveryAgentService.getById(id));
  }

  @GetMapping("/by-user/{userId}")
  public ResponseEntity<DeliveryAgentResponse> getByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(deliveryAgentService.getByUserId(userId));
  }

  @GetMapping
  public ResponseEntity<PageResponse<DeliveryAgentResponse>> search(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) UUID warehouseId,
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
        deliveryAgentService.search(
            search,
            warehouseId,
            status,
            taxId,
            createdFrom,
            createdTo,
            PageRequest.of(page, Math.min(size, 100), sort)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DeliveryAgentResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateDeliveryAgentRequest request) {
    return ResponseEntity.ok(deliveryAgentService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    deliveryAgentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
