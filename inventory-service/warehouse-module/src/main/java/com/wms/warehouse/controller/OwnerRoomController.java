package com.wms.warehouse.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.warehouse.dto.room.AddRoomCategoryRequest;
import com.wms.warehouse.dto.room.CreateRoomRequest;
import com.wms.warehouse.dto.room.RoomResponse;
import com.wms.warehouse.dto.room.UpdateRoomRequest;
import com.wms.warehouse.dto.room.UpdateRoomStatusRequest;
import com.wms.warehouse.service.RoomService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerRoomController {

  private final RoomService roomService;

  @PostMapping("/zones/{zoneId}/rooms")
  public ResponseEntity<RoomResponse> create(
      @PathVariable UUID zoneId, @Valid @RequestBody CreateRoomRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(roomService.create(zoneId, ownerId, request));
  }

  @PatchMapping("/rooms/{id}")
  public ResponseEntity<RoomResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateRoomRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(roomService.update(id, ownerId, request));
  }

  @PatchMapping("/rooms/{id}/status")
  public ResponseEntity<RoomResponse> updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateRoomStatusRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    return ResponseEntity.ok(roomService.updateStatus(id, ownerId, request));
  }

  @DeleteMapping("/rooms/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    UUID ownerId = UserContextHolder.get().userId();
    roomService.delete(id, ownerId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/rooms/{id}/categories")
  public ResponseEntity<Void> addCategory(
      @PathVariable UUID id, @Valid @RequestBody AddRoomCategoryRequest request) {
    UUID ownerId = UserContextHolder.get().userId();
    roomService.addCategory(id, ownerId, request.categoryId());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
