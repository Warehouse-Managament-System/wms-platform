package com.wms.reservation.repository;

import com.wms.reservation.entity.BookingExpiryNotification;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingExpiryNotificationRepository
    extends JpaRepository<BookingExpiryNotification, UUID> {

  Optional<BookingExpiryNotification> findByBookingId(UUID bookingId);

  boolean existsByBookingId(UUID bookingId);
}
