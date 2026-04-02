package com.wms.reservation.repository;

import com.wms.common.enums.BookingStatus;
import com.wms.reservation.entity.Booking;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository
    extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

  List<Booking> findByCustomerIdAndDeletedAtIsNull(UUID customerId);

  Optional<Booking> findByIdAndDeletedAtIsNull(UUID id);

  List<Booking> findByWarehouseIdAndDeletedAtIsNull(UUID warehouseId);

  boolean existsByRoomIdAndStatusInAndDeletedAtIsNull(
      UUID roomId, Collection<BookingStatus> statuses);

  boolean existsByZoneIdAndStatusInAndDeletedAtIsNull(
      UUID zoneId, Collection<BookingStatus> statuses);

  List<Booking> findByEndDateAndStatusAndDeletedAtIsNull(LocalDate endDate, BookingStatus status);

  List<Booking> findByEndDateBeforeAndStatusAndDeletedAtIsNull(
      LocalDate endDate, BookingStatus status);
}
