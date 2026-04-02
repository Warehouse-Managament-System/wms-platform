package com.wms.reservation.controller;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.BookingStatus;
import com.wms.common.security.UserContextHolder;
import com.wms.reservation.dto.BookingResponse;
import com.wms.reservation.dto.CreateBookingRequest;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.service.BookingService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/bookings")
@RequiredArgsConstructor
public class CustomerBookingController {

  private final BookingService bookingService;

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("createdAt", "startDate", "endDate", "totalPrice");

  @PostMapping
  public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
    UUID customerId = UserContextHolder.get().userId();

    Booking booking =
        bookingService.create(
            customerId,
            request.bookingType(),
            request.warehouseId(),
            request.roomId(),
            request.zoneId(),
            request.startDate(),
            request.endDate());

    return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(booking));
  }

  @GetMapping
  public ResponseEntity<PageResponse<BookingResponse>> list(
      @RequestParam(required = false) BookingStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    UUID customerId = UserContextHolder.get().userId();

    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      sortBy = "createdAt";
    }

    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    PageRequest pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(bookingService.listByCustomer(customerId, status, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookingResponse> getById(@PathVariable UUID id) {
    UUID customerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(bookingService.getById(id, customerId));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<BookingResponse> cancel(@PathVariable UUID id) {
    UUID customerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(bookingService.cancel(id, customerId));
  }
}
