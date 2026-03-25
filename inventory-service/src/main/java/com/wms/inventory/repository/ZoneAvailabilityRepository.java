package com.wms.inventory.repository;

import com.wms.inventory.entity.ZoneAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ZoneAvailabilityRepository extends JpaRepository<ZoneAvailability, UUID> {
    List<ZoneAvailability> findByZoneId(UUID zoneId);
}
