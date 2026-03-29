package com.wms.warehouse.repository;

import com.wms.warehouse.entity.ZoneCategory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneCategoryRepository extends JpaRepository<ZoneCategory, UUID> {
  boolean existsByZoneIdAndCategoryId(UUID zoneId, UUID categoryId);
}
