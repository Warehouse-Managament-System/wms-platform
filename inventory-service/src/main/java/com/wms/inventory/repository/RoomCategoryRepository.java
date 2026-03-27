package com.wms.inventory.repository;

import com.wms.inventory.entity.RoomCategory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomCategoryRepository extends JpaRepository<RoomCategory, UUID> {

  boolean existsByRoomIdAndCategoryId(UUID roomId, UUID categoryId);
}
