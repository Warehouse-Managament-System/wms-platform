package com.wms.goods.controller;

import com.wms.goods.dto.receipt.*;
import com.wms.goods.service.GoodsReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff/goods")
@RequiredArgsConstructor
public class StaffGoodsController {

    private final GoodsReceiptService service;

    @PostMapping("/receipts")
    public GoodsReceiptResponse createReceipt(@RequestBody CreateGoodsReceiptRequest request) {
        return service.createReceipt(getStaffId(), request);
    }

    @PostMapping("/receipts/{receiptId}/items")
    public void recordItem(
        @PathVariable UUID receiptId,
        @RequestBody RecordReceiptItemRequest request
    ) {
        service.recordItem(receiptId, getStaffId(), request);
    }

    private UUID getStaffId() {
        return UUID.randomUUID(); // replace with UserContextHolder
    }
}
