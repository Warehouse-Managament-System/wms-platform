package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.delivery.AssignStaffRequest;
import com.wms.delivery.dto.delivery.DeliveryRequestResponse;
import com.wms.delivery.service.DeliveryRequestService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/deliveries")
@RequiredArgsConstructor
public class OwnerDeliveryController {

  private final DeliveryRequestService deliveryRequestService;

  @GetMapping
  public ResponseEntity<List<DeliveryRequestResponse>> listByWarehouse(
      @RequestParam UUID warehouseId) {
    return ResponseEntity.ok(deliveryRequestService.listByWarehouse(warehouseId));
  }

  @PatchMapping("/{id}/confirm")
  public ResponseEntity<DeliveryRequestResponse> confirm(@PathVariable UUID id) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(deliveryRequestService.confirm(id, ownerId));
  }

  @PatchMapping("/{id}/assign")
  public ResponseEntity<DeliveryRequestResponse> assignStaff(
      @PathVariable UUID id, @Valid @RequestBody AssignStaffRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(
        deliveryRequestService.assignStaff(id, ownerId, request.staffId()));
  }
}
