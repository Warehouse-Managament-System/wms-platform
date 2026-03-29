package com.wms.goods.controller;

import com.wms.goods.dto.ApproveGoodsImportRequest;
import com.wms.goods.dto.RejectGoodsImportRequest;
import com.wms.goods.service.GoodsImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owner/goods")
@RequiredArgsConstructor
public class OwnerGoodsController {

    private final GoodsImportService service;

    @PatchMapping("/imports/{id}/approve")
    public void approve(
        @PathVariable UUID id,
        @RequestBody @Valid ApproveGoodsImportRequest request
    ) {
        service.approve(id, getOwnerId(), request);
    }

    @PatchMapping("/imports/{id}/reject")
    public void reject(
        @PathVariable UUID id,
        @RequestBody @Valid RejectGoodsImportRequest request
    ) {
        service.reject(id, getOwnerId(), request);
    }

    private UUID getOwnerId() {
        return UUID.randomUUID();
    }
}
