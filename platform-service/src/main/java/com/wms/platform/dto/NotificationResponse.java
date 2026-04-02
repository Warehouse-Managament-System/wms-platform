package com.wms.platform.dto;

import com.wms.platform.entity.Notification;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    UUID userId,
    String type,
    String message,
    boolean read,
    Instant readAt,
    String link,
    Instant createdAt) {
  public static NotificationResponse from(Notification n) {
    return new NotificationResponse(
        n.getId(),
        n.getUserId(),
        n.getType(),
        n.getMessage(),
        n.isRead(),
        n.getReadAt(),
        n.getLink(),
        n.getCreatedAt());
  }
}
