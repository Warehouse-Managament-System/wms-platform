package com.wms.identity.repository;

import com.wms.common.enums.UserRole;
import com.wms.identity.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

  Optional<User> findByEmail(String email);

  Optional<User> findByEmailAndDeletedAtIsNull(String email);

  Optional<User> findByIdAndDeletedAtIsNull(UUID id);

  boolean existsByEmailAndDeletedAtIsNull(String email);

  boolean existsByRoleAndDeletedAtIsNull(UserRole role);
}
