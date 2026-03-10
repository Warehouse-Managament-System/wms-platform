package com.wms.identity.controller;

import com.wms.identity.dto.request.CreateWarehouseOwnerRequest;
import com.wms.identity.dto.request.RejectWarehouseOwnerRequest;
import com.wms.identity.dto.request.UpdateWarehouseOwnerRequest;
import com.wms.identity.dto.response.WarehouseOwnerResponse;
import com.wms.identity.service.WarehouseOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/warehouse-owners")
@RequiredArgsConstructor
public class WarehouseOwnerController {

    private final WarehouseOwnerService service;

    @PostMapping
    public void create(@RequestBody CreateWarehouseOwnerRequest request) {
        service.create(request);
    }

    @GetMapping("/{id}")
    public WarehouseOwnerResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<WarehouseOwnerResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/{id}/approve")
    public void approve(
        @PathVariable UUID id,
        @RequestParam UUID adminId) {

        service.approve(id, adminId);
    }

    @PostMapping("/{id}/reject")
    public void reject(
        @PathVariable UUID id,
        @RequestParam UUID adminId,
        @RequestBody RejectWarehouseOwnerRequest request) {

        service.reject(id, adminId, request.getReason());
    }
    @PutMapping("/{id}")
    public void update(
        @PathVariable UUID id,
        @RequestBody UpdateWarehouseOwnerRequest request) {

        service.update(id, request);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
