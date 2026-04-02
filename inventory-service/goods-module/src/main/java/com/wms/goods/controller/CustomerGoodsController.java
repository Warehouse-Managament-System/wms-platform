package com.wms.goods.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.goods.dto.GoodsImportResponse;
import com.wms.goods.dto.GoodsItemResponse;
import com.wms.goods.service.GoodsImportService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerGoodsController {

    private final GoodsImportService service;

    @GetMapping("/goods/imports")
    public ResponseEntity<List<GoodsImportResponse>> listImports() {
        UUID customerId = UserContextHolder.get().userId();
        return ResponseEntity.ok(service.listByCustomer(customerId));
    }

    @GetMapping("/goods/items")
    public ResponseEntity<List<GoodsItemResponse>> listItems(@RequestParam UUID bookingId) {
        return ResponseEntity.ok(service.listItemsByBooking(bookingId));
    }

    @PostMapping("/bookings/{bookingId}/goods/import")
    public GoodsImportResponse upload(
        @PathVariable UUID bookingId,
        @RequestParam("file") MultipartFile file
    ) throws Exception {
        UUID customerId = UserContextHolder.get().userId();
        return service.upload(
            bookingId,
            customerId,
            file.getOriginalFilename(),
            file.getInputStream()
        );
    }
}
