package com.wms.delivery.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.delivery.dto.delivery.AddDeliveryItemRequest;
import com.wms.delivery.dto.delivery.CreateDeliveryRequest;
import com.wms.delivery.service.DeliveryRequestService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/delivery/requests")
@RequiredArgsConstructor
public class DeliveryRequestController {

  private final DeliveryRequestService service;

  @PostMapping
  public ResponseEntity<UUID> create(@Valid @RequestBody CreateDeliveryRequest request) {
    UUID customerId = UserContextHolder.get().userId();
    UUID id =
        service.createRequest(
            request.bookingId(),
            customerId,
            request.address(),
            request.city(),
            request.country(),
            request.requestedDate());
    return ResponseEntity.status(HttpStatus.CREATED).body(id);
  }

  @PostMapping("/{id}/items")
  public ResponseEntity<Void> addItem(
      @PathVariable UUID id, @Valid @RequestBody AddDeliveryItemRequest request) {
    service.addItem(id, request.goodsItemId(), request.qty());
    return ResponseEntity.ok().build();
  }
}
