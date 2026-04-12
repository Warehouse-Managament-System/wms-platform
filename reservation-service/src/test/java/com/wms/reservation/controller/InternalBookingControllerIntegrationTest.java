package com.wms.reservation.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.wms.common.enums.BookingStatus;
import com.wms.common.enums.BookingType;
import com.wms.common.exception.GlobalExceptionHandler;
import com.wms.reservation.entity.Booking;
import com.wms.reservation.repository.BookingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("InternalBookingController integration test")
class InternalBookingControllerIntegrationTest {

  private MockMvc mockMvc;
  private BookingRepository bookingRepository;

  @BeforeEach
  void setUp() {
    bookingRepository = mock(BookingRepository.class);
    InternalBookingController controller = new InternalBookingController(bookingRepository);
    mockMvc = standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
  }

  @Test
  @DisplayName("GET /api/v1/internal/bookings/{id}/status returns 200 with status payload")
  void getStatus_returns200() throws Exception {
    UUID bookingId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    UUID warehouseId = UUID.randomUUID();

    Booking booking =
        Booking.builder()
            .customerId(customerId)
            .warehouseId(warehouseId)
            .bookingType(BookingType.SHORT_TERM)
            .roomId(UUID.randomUUID())
            .startDate(LocalDate.of(2026, 5, 1))
            .endDate(LocalDate.of(2026, 5, 10))
            .surfaceArea(BigDecimal.valueOf(50))
            .totalPrice(BigDecimal.valueOf(450))
            .status(BookingStatus.CONFIRMED)
            .build();
    booking.setId(bookingId);

    when(bookingRepository.findByIdAndDeletedAtIsNull(bookingId)).thenReturn(Optional.of(booking));

    mockMvc
        .perform(get("/api/v1/internal/bookings/{id}/status", bookingId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONFIRMED"))
        .andExpect(jsonPath("$.customerId").value(customerId.toString()))
        .andExpect(jsonPath("$.warehouseId").value(warehouseId.toString()));
  }

  @Test
  @DisplayName("GET /api/v1/internal/bookings/{id}/status returns 404 when the booking is missing")
  void getStatus_returns404_whenMissing() throws Exception {
    UUID bookingId = UUID.randomUUID();
    when(bookingRepository.findByIdAndDeletedAtIsNull(bookingId)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/v1/internal/bookings/{id}/status", bookingId))
        .andExpect(status().isNotFound());
  }
}
