package com.wms.warehouse.service;

import com.wms.common.dto.RoomAvailabilityResponse;
import com.wms.common.enums.RoomStatus;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.warehouse.entity.Room;
import com.wms.warehouse.entity.Zone;
import com.wms.warehouse.repository.RoomRepository;
import com.wms.warehouse.repository.ZoneRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityService {

  private final RoomRepository roomRepository;
  private final ZoneRepository zoneRepository;

  @Transactional(readOnly = true)
  public RoomAvailabilityResponse checkAvailability(
      UUID id, LocalDate startDate, LocalDate endDate) {

    Optional<Room> roomOpt = roomRepository.findByIdAndDeletedAtIsNull(id);
    if (roomOpt.isPresent()) {
      return checkRoomAvailability(roomOpt.get());
    }

    Zone zone =
        zoneRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Room or Zone", id));

    return checkZoneAvailability(zone);
  }

  private RoomAvailabilityResponse checkRoomAvailability(Room room) {
    Zone zone = room.getZone();

    boolean available = room.getStatus() == RoomStatus.AVAILABLE;
    String reason = available ? null : "Room status is " + room.getStatus();

    return new RoomAvailabilityResponse(
        room.getId(),
        available,
        reason,
        room.getTotalSurfaceArea(),
        room.getPricePerSqmDaily(),
        room.getPricePerSqmWeekly(),
        room.getPricePerSqmMonthly(),
        zone.getDiscountPercentage(),
        zone.getWarehouse().getDiscountPercentage());
  }

  private RoomAvailabilityResponse checkZoneAvailability(Zone zone) {
    boolean available =
        zone.getStatus() == com.wms.common.enums.ZoneStatus.ACTIVE;
    String reason = available ? null : "Zone status is " + zone.getStatus();

    return new RoomAvailabilityResponse(
        zone.getId(),
        available,
        reason,
        zone.getTotalSurfaceArea(),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        zone.getDiscountPercentage(),
        zone.getWarehouse().getDiscountPercentage());
  }
}
