package com.wms.reservation.controller;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.reservation.dto.BookingStatusResponse;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.repository.BookingRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/bookings")
@RequiredArgsConstructor
public class InternalBookingController {

  private final BookingRepository bookingRepository;

  @GetMapping("/{id}/status")
  public ResponseEntity<BookingStatusResponse> getStatus(@PathVariable UUID id) {
    Booking booking =
        bookingRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new EntityNotFoundException("Booking", id));

    return ResponseEntity.ok(
        new BookingStatusResponse(
            booking.getStatus(), booking.getCustomerId(), booking.getWarehouseId()));
  }
}
