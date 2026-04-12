package com.wms.identity.feign;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for verifying that a warehouse exists in inventory-service. Used when creating or
 * updating staff and delivery agents to make sure their assigned warehouseId actually points to a
 * real warehouse.
 */
@FeignClient(name = "inventory")
public interface WarehouseClient {

  /**
   * Returns 204 if the warehouse exists. Throws (via Feign) if it does not — the calling service
   * should catch the exception and translate it to its own EntityNotFoundException.
   */
  @GetMapping("/api/v1/internal/warehouses/{id}/exists")
  void verifyExists(@PathVariable("id") UUID id);
}
