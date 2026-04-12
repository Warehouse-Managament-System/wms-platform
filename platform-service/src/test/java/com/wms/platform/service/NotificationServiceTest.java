package com.wms.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.platform.entity.Notification;
import com.wms.platform.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService unit tests")
class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;
  @Mock private EmailService emailService;

  @InjectMocks private NotificationService notificationService;

  @Test
  @DisplayName("create persists a notification with the supplied user, type, and message")
  void create_savesNotification() {
    UUID userId = UUID.randomUUID();

    notificationService.create(userId, "BOOKING_CONFIRMED", "Your booking was confirmed");

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    Notification saved = captor.getValue();
    assertThat(saved.getUserId()).isEqualTo(userId);
    assertThat(saved.getType()).isEqualTo("BOOKING_CONFIRMED");
    assertThat(saved.getMessage()).isEqualTo("Your booking was confirmed");
  }

  @Test
  @DisplayName("createAndEmail persists the notification AND sends an email through EmailService")
  void createAndEmail_persistsAndSends() {
    UUID userId = UUID.randomUUID();

    notificationService.createAndEmail(
        userId, "alice@example.com", "OWNER_APPROVED", "Welcome", "You are approved");

    verify(notificationRepository).save(any(Notification.class));
    verify(emailService).send("alice@example.com", "Welcome", "You are approved");
  }

  @Test
  @DisplayName("markRead throws EntityNotFoundException when the notification does not exist")
  void markRead_throws_whenNotFound() {
    UUID id = UUID.randomUUID();
    when(notificationRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.markRead(id))
        .isInstanceOf(EntityNotFoundException.class);

    verify(notificationRepository, never()).save(any(Notification.class));
  }
}
