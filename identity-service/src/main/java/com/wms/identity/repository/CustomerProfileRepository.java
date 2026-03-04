package com.wms.identity.repository;


import com.wms.identity.entity.CustomerProfile;
import com.wms.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProfileRepository
    extends JpaRepository<CustomerProfile, UUID> {

    Optional<CustomerProfile> findByUser(User user);

    Optional<CustomerProfile> findByTaxId(String taxId);

    boolean existsByTaxId(String taxId);

}
