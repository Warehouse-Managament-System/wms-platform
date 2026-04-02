package com.wms.reservation.controller;

import com.wms.common.dto.PageResponse;
import com.wms.reservation.dto.invoice.InvoiceResponse;
import com.wms.reservation.service.InvoiceService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
      @RequestParam UUID warehouseId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      sortBy = "createdAt";
    }

    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    PageRequest pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(invoiceService.listByWarehouse(warehouseId, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<InvoiceResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(invoiceService.getByIdForOwner(id));
  }
}
