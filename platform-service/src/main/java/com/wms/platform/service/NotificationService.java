package com.wms.platform.service;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.platform.dto.NotificationResponse;
import com.wms.platform.entity.Notification;
import com.wms.platform.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final EmailService emailService;

  @Transactional
  public void create(UUID userId, String type, String message) {
    Notification notification =
        Notification.builder().userId(userId).type(type).message(message).build();

    notificationRepository.save(notification);
    log.info("Notification created: userId={}, type={}", userId, type);
  }

  @Transactional
  public void createAndEmail(
      UUID userId, String email, String type, String subject, String message) {
    create(userId, type, message);
    emailService.send(email, subject, message);
  }

  @Transactional(readOnly = true)
  public List<NotificationResponse> listByUser(UUID userId) {
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(NotificationResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public long countUnread(UUID userId) {
    return notificationRepository.countByUserIdAndReadFalse(userId);
  }

  @Transactional
  public void markRead(UUID id) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification", id));

    notification.setRead(true);
    notification.setReadAt(Instant.now());
    notificationRepository.save(notification);
  }
}
