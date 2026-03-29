package com.wms.warehouse.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.RoomStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.warehouse.dto.room.CreateRoomRequest;
import com.wms.warehouse.dto.room.RoomResponse;
import com.wms.warehouse.dto.room.UpdateRoomRequest;
import com.wms.warehouse.dto.room.UpdateRoomStatusRequest;
import com.wms.warehouse.entity.Category;
import com.wms.warehouse.entity.Room;
import com.wms.warehouse.entity.RoomCategory;
import com.wms.warehouse.entity.Zone;
import com.wms.warehouse.repository.CategoryRepository;
import com.wms.warehouse.repository.RoomCategoryRepository;
import com.wms.warehouse.repository.RoomRepository;
import com.wms.warehouse.repository.ZoneRepository;
import com.wms.warehouse.specification.RoomSpecification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;
  private final RoomCategoryRepository roomCategoryRepository;
  private final ZoneRepository zoneRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  public RoomResponse create(UUID zoneId, UUID ownerId, CreateRoomRequest request) {
    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new EntityNotFoundException("Zone", zoneId));

    verifyOwnership(zone, ownerId);

    Room room =
        Room.builder()
            .zone(zone)
            .name(request.name())
            .description(request.description())
            .totalSurfaceArea(request.totalSurfaceArea())
            .pricePerSqmDaily(request.pricePerSqmDaily())
            .pricePerSqmWeekly(request.pricePerSqmWeekly())
            .pricePerSqmMonthly(request.pricePerSqmMonthly())
            .status(RoomStatus.AVAILABLE)
            .build();

    return RoomResponse.from(roomRepository.save(room));
  }

  @Transactional
  public RoomResponse update(UUID id, UUID ownerId, UpdateRoomRequest request) {
    Room room = findOrThrow(id);
    verifyOwnership(room.getZone(), ownerId);
    checkNotBooked(room);

    if (request.name() != null) room.setName(request.name());
    if (request.description() != null) room.setDescription(request.description());
    if (request.totalSurfaceArea() != null) room.setTotalSurfaceArea(request.totalSurfaceArea());
    if (request.pricePerSqmDaily() != null) room.setPricePerSqmDaily(request.pricePerSqmDaily());
    if (request.pricePerSqmWeekly() != null) room.setPricePerSqmWeekly(request.pricePerSqmWeekly());
    if (request.pricePerSqmMonthly() != null)
      room.setPricePerSqmMonthly(request.pricePerSqmMonthly());

    return RoomResponse.from(roomRepository.save(room));
  }

  @Transactional
  public RoomResponse updateStatus(UUID id, UUID ownerId, UpdateRoomStatusRequest request) {
    Room room = findOrThrow(id);
    verifyOwnership(room.getZone(), ownerId);
    checkNotBooked(room);

    room.setStatus(request.status());
    return RoomResponse.from(roomRepository.save(room));
  }

  @Transactional
  public void delete(UUID id, UUID ownerId) {
    Room room = findOrThrow(id);
    verifyOwnership(room.getZone(), ownerId);
    checkNotBooked(room);

    room.setDeletedAt(Instant.now());
    roomRepository.save(room);
  }

  @Transactional(readOnly = true)
  public PageResponse<RoomResponse> listByZone(UUID zoneId, RoomStatus status, Pageable pageable) {
    Specification<Room> spec =
        RoomSpecification.hasZoneId(zoneId).and(RoomSpecification.isNotDeleted());

    if (status != null) {
      spec = spec.and(RoomSpecification.hasStatus(status));
    }

    return PageResponse.from(roomRepository.findAll(spec, pageable).map(RoomResponse::from));
  }

  @Transactional
  public void addCategory(UUID roomId, UUID ownerId, UUID categoryId) {
    Room room = findOrThrow(roomId);
    verifyOwnership(room.getZone(), ownerId);

    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));

    if (roomCategoryRepository.existsByRoomIdAndCategoryId(roomId, categoryId)) {
      throw new ResourceConflictException("Category already linked to this room");
    }

    roomCategoryRepository.save(RoomCategory.builder().room(room).category(category).build());
  }

  private Room findOrThrow(UUID id) {
    return roomRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new EntityNotFoundException("Room", id));
  }

  private void verifyOwnership(Zone zone, UUID ownerId) {
    if (!zone.getWarehouse().getOwnerId().equals(ownerId)) {
      throw new EntityNotFoundException("Room", zone.getId());
    }
  }

  private void checkNotBooked(Room room) {
    if (room.getStatus() == RoomStatus.BOOKED) {
      throw new BusinessRuleException("Cannot modify a booked room");
    }
  }
}
