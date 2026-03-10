package com.wms.identity.service;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import com.wms.identity.enums.Status;
import com.wms.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public User createUser(
        String email,
        String password,
        String firstName,
        String lastName,
        UserRole role) {

        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user =
            User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .deletedAt(null)
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeletedAt(Instant.now());
    }
}
