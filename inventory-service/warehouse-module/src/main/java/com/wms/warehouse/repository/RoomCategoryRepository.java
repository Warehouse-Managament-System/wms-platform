package com.wms.warehouse.repository;

import com.wms.warehouse.entity.RoomCategory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomCategoryRepository extends JpaRepository<RoomCategory, UUID> {

  boolean existsByRoomIdAndCategoryId(UUID roomId, UUID categoryId);
}
