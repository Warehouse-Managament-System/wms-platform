package com.wms.reservation.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.BookingStatus;
import com.wms.reservation.dto.BookingResponse;
import com.wms.reservation.dto.RejectBookingRequest;
import com.wms.reservation.service.BookingService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/bookings")
@RequiredArgsConstructor
public class OwnerBookingController {

  private final BookingService bookingService;

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "startDate", "endDate", "totalPrice");

  @GetMapping
  public ResponseEntity<PageResponse<BookingResponse>> list(
      @RequestParam UUID warehouseId,
      @RequestParam(required = false) BookingStatus status,
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
    return ResponseEntity.ok(bookingService.listByWarehouse(warehouseId, status, pageable));
  }

  @PatchMapping("/{id}/confirm")
  public ResponseEntity<BookingResponse> confirm(@PathVariable UUID id) {
    return ResponseEntity.ok(bookingService.confirm(id));
  }

  @PatchMapping("/{id}/reject")
  public ResponseEntity<BookingResponse> reject(
      @PathVariable UUID id, @Valid @RequestBody RejectBookingRequest request) {
    return ResponseEntity.ok(bookingService.reject(id, request.reason()));
  }
}
