package com.wms.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.identity.dto.user.UpdateUserProfileRequest;
import com.wms.identity.dto.user.UserResponse;
import com.wms.identity.entity.User;
import com.wms.identity.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService unit tests")
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User existingUser;
  private UUID existingUserId;

  @BeforeEach
  void setUp() {
    existingUserId = UUID.randomUUID();
    existingUser =
        User.builder()
            .email("alice@example.com")
            .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345")
            .firstName("Alice")
            .lastName("Anderson")
            .role(UserRole.CUSTOMER)
            .status(UserStatus.ACTIVE)
            .build();
    existingUser.setId(existingUserId);
  }

  @Test
  @DisplayName("getUserById returns the user when it exists and is not soft-deleted")
  void getUserById_returnsUser_whenFound() {
    when(userRepository.findByIdAndDeletedAtIsNull(existingUserId))
        .thenReturn(Optional.of(existingUser));

    UserResponse response = userService.getUserById(existingUserId);

    assertThat(response.id()).isEqualTo(existingUserId);
    assertThat(response.email()).isEqualTo("alice@example.com");
    assertThat(response.firstName()).isEqualTo("Alice");
    assertThat(response.role()).isEqualTo(UserRole.CUSTOMER);
    assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("getUserById throws EntityNotFoundException when the user does not exist")
  void getUserById_throws_whenNotFound() {
    UUID missingId = UUID.randomUUID();
    when(userRepository.findByIdAndDeletedAtIsNull(missingId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserById(missingId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(missingId.toString());
  }

  @Test
  @DisplayName("updateProfile updates first and last name and saves the user")
  void updateProfile_updatesFieldsAndSaves() {
    when(userRepository.findByEmailAndDeletedAtIsNull("alice@example.com"))
        .thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    UpdateUserProfileRequest request = new UpdateUserProfileRequest("Alicia", "Smith");
    UserResponse response = userService.updateProfile("alice@example.com", request);

    assertThat(response.firstName()).isEqualTo("Alicia");
    assertThat(response.lastName()).isEqualTo("Smith");
    assertThat(existingUser.getFirstName()).isEqualTo("Alicia");
    assertThat(existingUser.getLastName()).isEqualTo("Smith");
    verify(userRepository).save(existingUser);
  }
}
