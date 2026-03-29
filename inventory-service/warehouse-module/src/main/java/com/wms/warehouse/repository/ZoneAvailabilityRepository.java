package com.wms.warehouse.repository;

import com.wms.warehouse.entity.ZoneAvailability;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneAvailabilityRepository extends JpaRepository<ZoneAvailability, UUID> {
  List<ZoneAvailability> findByZoneId(UUID zoneId);
}
