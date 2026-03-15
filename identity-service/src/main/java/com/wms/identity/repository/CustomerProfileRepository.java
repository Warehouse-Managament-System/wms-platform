package com.wms.identity.repository;


import com.wms.identity.entity.Customer;
import com.wms.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerProfileRepository
    extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByUser(User user);

    Optional<Customer> findByTaxId(String taxId);

    boolean existsByTaxId(String taxId);

}
