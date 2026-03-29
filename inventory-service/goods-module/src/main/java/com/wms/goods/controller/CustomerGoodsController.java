package com.wms.goods.controller;

import com.wms.goods.dto.GoodsImportResponse;
import com.wms.goods.service.GoodsImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerGoodsController {

    private final GoodsImportService service;

    @PostMapping("/bookings/{bookingId}/goods/import")
    public GoodsImportResponse upload(
        @PathVariable UUID bookingId,
        @RequestParam("file") MultipartFile file
    ) throws Exception {

        return service.upload(
            bookingId,
            getCustomerId(),
            file.getOriginalFilename(),
            file.getInputStream()
        );
    }

    // stub
    private UUID getCustomerId() {
        return UUID.randomUUID();
    }
}
