package com.wms.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.GlobalExceptionHandler;
import com.wms.identity.dto.user.UserResponse;
import com.wms.identity.service.UserService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("UserController integration test")
class UserControllerIntegrationTest {

  private MockMvc mockMvc;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    UserController controller = new UserController(userService);
    mockMvc = standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
  }

  @Test
  @DisplayName("GET /api/v1/users/{id} returns 200 with user JSON when the user exists")
  void getUserById_returns200WithUser() throws Exception {
    UUID id = UUID.randomUUID();
    UserResponse response =
        new UserResponse(
            id,
            "alice@example.com",
            "Alice",
            "Anderson",
            UserRole.CUSTOMER,
            UserStatus.ACTIVE,
            Instant.parse("2026-01-01T00:00:00Z"),
            Instant.parse("2026-01-02T00:00:00Z"));
    when(userService.getUserById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/users/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.firstName").value("Alice"))
        .andExpect(jsonPath("$.lastName").value("Anderson"))
        .andExpect(jsonPath("$.role").value("CUSTOMER"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  @DisplayName("GET /api/v1/users/{id} returns 404 when the user does not exist")
  void getUserById_returns404_whenNotFound() throws Exception {
    UUID missingId = UUID.randomUUID();
    when(userService.getUserById(any(UUID.class)))
        .thenThrow(new EntityNotFoundException("User", missingId));

    mockMvc.perform(get("/api/v1/users/{id}", missingId)).andExpect(status().isNotFound());
  }
}
