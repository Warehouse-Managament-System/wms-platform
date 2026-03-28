package com.wms.delivery.feign;

import com.wms.common.dto.GoodsItemAvailabilityResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory")
public interface InventoryClient {

  @GetMapping("/api/v1/internal/goods-items/{id}/available-qty")
  GoodsItemAvailabilityResponse getAvailableQty(@PathVariable UUID id);
}
