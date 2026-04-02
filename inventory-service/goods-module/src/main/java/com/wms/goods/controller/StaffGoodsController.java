package com.wms.goods.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.goods.dto.GoodsImportResponse;
import com.wms.goods.dto.receipt.*;
import com.wms.goods.service.GoodsImportService;
import com.wms.goods.service.GoodsReceiptService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff/goods")
@RequiredArgsConstructor
public class StaffGoodsController {

    private final GoodsImportService importService;
    private final GoodsReceiptService service;

    @GetMapping("/imports")
    public ResponseEntity<List<GoodsImportResponse>> listApprovedImports() {
        return ResponseEntity.ok(importService.listApproved());
    }

    @PostMapping("/receipts")
    public GoodsReceiptResponse createReceipt(@RequestBody CreateGoodsReceiptRequest request) {
        UUID staffId = UserContextHolder.get().userId();
        return service.createReceipt(staffId, request);
    }

    @PostMapping("/receipts/{receiptId}/items")
    public void recordItem(
        @PathVariable UUID receiptId,
        @RequestBody RecordReceiptItemRequest request
    ) {
        UUID staffId = UserContextHolder.get().userId();
        service.recordItem(receiptId, staffId, request);
    }
}
