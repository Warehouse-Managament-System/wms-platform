package com.wms.inventory.repository;

import com.wms.inventory.entity.Room;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {

  List<Room> findByZoneIdAndDeletedAtIsNull(UUID zoneId);

  Optional<Room> findByIdAndDeletedAtIsNull(UUID id);

  long countByZoneId(UUID zoneId);
}
