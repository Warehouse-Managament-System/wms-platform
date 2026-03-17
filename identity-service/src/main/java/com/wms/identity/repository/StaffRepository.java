package com.wms.identity.repository;

import com.wms.identity.entity.Staff;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StaffRepository
    extends JpaRepository<Staff, UUID>, JpaSpecificationExecutor<Staff> {

  boolean existsByUserId(UUID userId);

  Optional<Staff> findByUserId(UUID userId);
}
