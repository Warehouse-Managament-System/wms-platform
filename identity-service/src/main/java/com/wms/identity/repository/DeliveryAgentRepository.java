package com.wms.identity.repository;

import com.wms.identity.entity.DeliveryAgent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeliveryAgentRepository
    extends JpaRepository<DeliveryAgent, UUID>, JpaSpecificationExecutor<DeliveryAgent> {

  boolean existsByUserId(UUID userId);

  boolean existsByTaxId(String taxId);

  Optional<DeliveryAgent> findByUserId(UUID userId);
}
