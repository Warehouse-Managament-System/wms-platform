package com.wms.goods.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.goods.dto.ApproveGoodsImportRequest;
import com.wms.goods.dto.GoodsImportResponse;
import com.wms.goods.dto.RejectGoodsImportRequest;
import com.wms.goods.service.GoodsImportService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/goods")
@RequiredArgsConstructor
public class OwnerGoodsController {

    private final GoodsImportService service;

    @GetMapping("/imports")
    public ResponseEntity<List<GoodsImportResponse>> listImports() {
        return ResponseEntity.ok(service.listAll());
    }

    @PatchMapping("/imports/{id}/approve")
    public void approve(
        @PathVariable UUID id,
        @RequestBody @Valid ApproveGoodsImportRequest request
    ) {
        UUID ownerId = UserContextHolder.get().userId();
        service.approve(id, ownerId, request);
    }

    @PatchMapping("/imports/{id}/reject")
    public void reject(
        @PathVariable UUID id,
        @RequestBody @Valid RejectGoodsImportRequest request
    ) {
        UUID ownerId = UserContextHolder.get().userId();
        service.reject(id, ownerId, request);
    }
}
