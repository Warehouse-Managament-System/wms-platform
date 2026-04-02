package com.wms.reservation.dto;

import com.wms.common.enums.BookingStatus;
import com.wms.common.enums.BookingType;
import com.wms.reservation.entity.Booking;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookingResponse(
    UUID id,
    UUID customerId,
    UUID warehouseId,
    BookingType bookingType,
    UUID roomId,
    UUID zoneId,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal surfaceArea,
    BigDecimal totalPrice,
    BookingStatus status,
    Instant createdAt,
    Instant updatedAt) {
  public static BookingResponse from(Booking booking) {
    return new BookingResponse(
        booking.getId(),
        booking.getCustomerId(),
        booking.getWarehouseId(),
        booking.getBookingType(),
        booking.getRoomId(),
        booking.getZoneId(),
        booking.getStartDate(),
        booking.getEndDate(),
        booking.getSurfaceArea(),
        booking.getTotalPrice(),
        booking.getStatus(),
        booking.getCreatedAt(),
        booking.getUpdatedAt());
  }
}
