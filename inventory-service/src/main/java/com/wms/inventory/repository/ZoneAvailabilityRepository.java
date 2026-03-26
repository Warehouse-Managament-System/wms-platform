package com.wms.inventory.repository;

import com.wms.inventory.entity.ZoneAvailability;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneAvailabilityRepository extends JpaRepository<ZoneAvailability, UUID> {
  List<ZoneAvailability> findByZoneId(UUID zoneId);
}
