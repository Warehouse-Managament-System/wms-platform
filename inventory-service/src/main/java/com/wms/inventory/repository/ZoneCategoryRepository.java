package com.wms.inventory.repository;

import com.wms.inventory.entity.ZoneCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ZoneCategoryRepository extends JpaRepository<ZoneCategory, UUID> {
    boolean existsByZoneIdAndCategoryId(UUID zoneId, UUID categoryId);
}
