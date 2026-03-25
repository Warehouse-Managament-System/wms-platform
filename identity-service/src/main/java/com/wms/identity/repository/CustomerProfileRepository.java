package com.wms.identity.repository;

import com.wms.identity.entity.Customer;
import com.wms.identity.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProfileRepository
    extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

  Optional<Customer> findByUser(User user);

  Optional<Customer> findByUserId(UUID userId);

  boolean existsByUserId(UUID userId);

  boolean existsByTaxId(String taxId);

  boolean existsByTaxIdAndIdNot(String taxId, UUID id);
}
