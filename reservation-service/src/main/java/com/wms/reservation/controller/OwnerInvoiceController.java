package com.wms.reservation.controller;

import com.wms.common.dto.PageResponse;
import com.wms.reservation.dto.invoice.InvoiceResponse;
import com.wms.reservation.service.InvoiceService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/owner/invoices")
@RequiredArgsConstructor
public class OwnerInvoiceController {

  private final InvoiceService invoiceService;

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "amount", "dueDate", "status");

  @GetMapping
  public ResponseEntity<PageResponse<InvoiceResponse>> list(
      @RequestParam UUID warehouseId, Pageable pageable) {
    return ResponseEntity.ok(invoiceService.listByWarehouse(warehouseId, pageable));
  }
}
