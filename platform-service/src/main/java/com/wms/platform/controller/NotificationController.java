package com.wms.platform.controller;

import com.wms.common.security.UserContextHolder;
import com.wms.platform.dto.NotificationResponse;
import com.wms.platform.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> list() {
    UUID userId = UserContextHolder.get().userId();
    return ResponseEntity.ok(notificationService.listByUser(userId));
  }

  @GetMapping("/unread-count")
  public ResponseEntity<Map<String, Long>> unreadCount() {
    UUID userId = UserContextHolder.get().userId();
    long count = notificationService.countUnread(userId);
    return ResponseEntity.ok(Map.of("count", count));
  }

  @PatchMapping("/{id}/read")
  public ResponseEntity<Void> markRead(@PathVariable UUID id) {
    notificationService.markRead(id);
    return ResponseEntity.ok().build();
  }
}
