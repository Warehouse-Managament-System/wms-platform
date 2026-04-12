package com.wms.delivery.feign;

import com.wms.delivery.feign.dto.BookingStatusResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for verifying that a booking exists in reservation-service before creating a
 * delivery request against it.
 */
@FeignClient(name = "reservation", contextId = "deliveryBookingClient")
public interface BookingClient {

  @GetMapping("/api/v1/internal/bookings/{id}/status")
  BookingStatusResponse getStatus(@PathVariable("id") UUID id);
}
