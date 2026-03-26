package com.wms.inventory.service;

import com.wms.common.enums.RoomStatus;
import com.wms.inventory.entity.Room;
import com.wms.inventory.repository.RoomRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  // Create or update room
  public Room save(Room room) {
    // If updating, check booking status
    if (room.getId() != null) {
      Room existing = getById(room.getId());
      if (existing != null && existing.getStatus() == RoomStatus.BOOKED) {
        throw new IllegalStateException("Cannot modify a room that is currently BOOKED.");
      }
    }
    return roomRepository.save(room);
  }

  // Get all active rooms
  public List<Room> getAll() {
    return roomRepository.findByDeletedAtIsNull();
  }

  // Get single room by id
  public Room getById(UUID id) {
    return roomRepository.findByIdAndDeletedAtIsNull(id);
  }

  // Soft delete room
  public void delete(UUID id) {
    Room room = getById(id);
    if (room == null) return;

    if (room.getStatus() == RoomStatus.BOOKED) {
      throw new IllegalStateException("Cannot delete a room that is currently BOOKED.");
    }

    room.softDelete();
    roomRepository.save(room);
  }
}
