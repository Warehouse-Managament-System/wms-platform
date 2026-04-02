package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.delivery.DeliveryRequestResponse;
import com.wms.delivery.service.DeliveryRequestService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/deliveries")
@RequiredArgsConstructor
public class CustomerDeliveryController {

  private final DeliveryRequestService service;

  @GetMapping
  public ResponseEntity<List<DeliveryRequestResponse>> list() {
    UUID customerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(service.listByCustomer(customerId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DeliveryRequestResponse> getById(@PathVariable UUID id) {
    UUID customerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(service.getByIdForCustomer(id, customerId));
  }
}
