package com.wms.inventory.repository;

import com.wms.inventory.entity.Room;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

  // Fetch all rooms that are not soft-deleted
  List<Room> findByDeletedAtIsNull();

  // Optional: find by id excluding soft-deleted
  Room findByIdAndDeletedAtIsNull(UUID id);
}
