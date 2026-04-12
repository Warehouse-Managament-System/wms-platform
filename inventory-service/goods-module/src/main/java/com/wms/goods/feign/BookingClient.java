package com.wms.goods.feign;

import com.wms.goods.feign.dto.BookingStatusResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for verifying that a booking exists in reservation-service. Returns the booking
 * status payload, which we use to confirm the booking exists, that it belongs to the customer
 * making the request, and to fill in the warehouseId on goods records.
 */
@FeignClient(name = "reservation", contextId = "goodsBookingClient")
public interface BookingClient {

  @GetMapping("/api/v1/internal/bookings/{id}/status")
  BookingStatusResponse getStatus(@PathVariable("id") UUID id);
}
