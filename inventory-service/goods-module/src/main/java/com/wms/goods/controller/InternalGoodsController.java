package com.wms.goods.controller;

import com.wms.common.dto.GoodsItemAvailabilityResponse;
import com.wms.goods.service.GoodsReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/goods-items")
@RequiredArgsConstructor
public class InternalGoodsController {

    private final GoodsReceiptService service;

    @GetMapping("/{id}/available-qty")
    public GoodsItemAvailabilityResponse getAvailableQty(@PathVariable UUID id) {
        return service.getAvailability(id);
    }
}
