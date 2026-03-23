package com.wms.identity.config;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import com.wms.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdminProperties.class)
public class SuperAdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SuperAdminProperties properties;

  @Value("${wms.production:false}")
  private boolean production;

  @Override
  @Transactional
  public void run(@NonNull ApplicationArguments args) {
    if (userRepository.existsByRoleAndDeletedAtIsNull(UserRole.SUPER_ADMIN)) {
      log.info("Super admin already exists, skipping initialization");
      return;
    }

    if (properties.getEmail() == null
        || properties.getEmail().isBlank()
        || properties.getPassword() == null
        || properties.getPassword().isBlank()) {

      if (production) {
        log.error(
            "PRODUCTION MODE: Super admin credentials not configured. "
                + "Set SUPER_ADMIN_EMAIL and SUPER_ADMIN_PASSWORD.");
      } else {
        log.warn("Super admin credentials not configured, skipping.");
      }

      return;
    }

    if (production) {
      log.info("Creating super admin account (production mode)");
    } else {
      log.info("Creating super admin account (dev mode — using default credentials)");
    }

    User superAdmin =
        User.builder()
            .email(properties.getEmail())
            .password(passwordEncoder.encode(properties.getPassword()))
            .firstName(properties.getFirstName())
            .lastName(properties.getLastName())
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();

    userRepository.save(superAdmin);
    log.info("Super admin account created successfully");
  }
}
