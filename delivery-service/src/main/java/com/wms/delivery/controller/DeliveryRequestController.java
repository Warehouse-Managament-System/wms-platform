package com.wms.delivery.controller;

import com.wms.delivery.service.DeliveryRequestService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/delivery/requests")
@RequiredArgsConstructor
public class DeliveryRequestController {

  private final DeliveryRequestService service;

  @PostMapping
  public UUID create(
      @RequestParam UUID bookingId,
      @RequestParam String address,
      @RequestParam String city,
      @RequestParam String country,
      @RequestParam String requestedDate) {
    return service.createRequest(
        bookingId, getCustomerId(), address, city, country, LocalDate.parse(requestedDate));
  }

  @PostMapping("/{id}/items")
  public void addItem(
      @PathVariable UUID id, @RequestParam UUID goodsItemId, @RequestParam BigDecimal qty) {
    service.addItem(id, goodsItemId, qty);
  }

  @PostMapping("/items/{itemId}/pick")
  public void pickItem(@PathVariable UUID itemId, @RequestParam BigDecimal qty) {
    service.pickItem(itemId, getStaffId(), qty);
  }

  private UUID getCustomerId() {
    return UUID.randomUUID();
  }

  private UUID getStaffId() {
    return UUID.randomUUID();
  }
}
