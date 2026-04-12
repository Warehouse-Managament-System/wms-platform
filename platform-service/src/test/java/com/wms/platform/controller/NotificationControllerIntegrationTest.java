package com.wms.platform.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.GlobalExceptionHandler;
import com.wms.platform.service.NotificationService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("NotificationController integration test")
class NotificationControllerIntegrationTest {

  private MockMvc mockMvc;
  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    notificationService = mock(NotificationService.class);
    NotificationController controller = new NotificationController(notificationService);
    mockMvc =
        standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
  }

  @Test
  @DisplayName("PATCH /api/v1/notifications/{id}/read returns 200 and delegates to the service")
  void markRead_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(notificationService).markRead(id);

    mockMvc.perform(patch("/api/v1/notifications/{id}/read", id)).andExpect(status().isOk());

    verify(notificationService).markRead(id);
  }

  @Test
  @DisplayName("PATCH /api/v1/notifications/{id}/read returns 404 when notification is missing")
  void markRead_returns404_whenMissing() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new EntityNotFoundException("Notification", id)).when(notificationService).markRead(id);

    mockMvc
        .perform(patch("/api/v1/notifications/{id}/read", id))
        .andExpect(status().isNotFound());
  }
}
