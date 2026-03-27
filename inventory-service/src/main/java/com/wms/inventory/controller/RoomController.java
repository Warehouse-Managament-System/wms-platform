package com.wms.inventory.controller;

import com.wms.inventory.entity.Room;
import com.wms.inventory.service.RoomService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @GetMapping
  public List<Room> getAllRooms() {
    return roomService.getAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Room> getRoom(@PathVariable UUID id) {
    Room room = roomService.getById(id);
    return room != null ? ResponseEntity.ok(room) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public Room createRoom(@RequestBody Room room) {
    return roomService.save(room);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateRoom(@PathVariable UUID id, @RequestBody Room updatedRoom) {
    try {
      updatedRoom.setId(id);
      Room room = roomService.save(updatedRoom);
      return ResponseEntity.ok(room);
    } catch (IllegalStateException ex) {
      return ResponseEntity.badRequest().body(ex.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteRoom(@PathVariable UUID id) {
    try {
      roomService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalStateException ex) {
      return ResponseEntity.badRequest().body(ex.getMessage());
    }
  }
}
