package com.wms.delivery.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.GlobalExceptionHandler;
import com.wms.delivery.dto.delivery.AddDeliveryItemRequest;
import com.wms.delivery.service.DeliveryRequestService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("DeliveryRequestController integration test")
class DeliveryRequestControllerIntegrationTest {

  private MockMvc mockMvc;
  private DeliveryRequestService deliveryRequestService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    deliveryRequestService = mock(DeliveryRequestService.class);
    DeliveryRequestController controller = new DeliveryRequestController(deliveryRequestService);
    mockMvc = standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
  }

  @Test
  @DisplayName("POST /api/v1/delivery/requests/{id}/items returns 200 and delegates to the service")
  void addItem_returns200_andDelegates() throws Exception {
    UUID requestId = UUID.randomUUID();
    UUID goodsItemId = UUID.randomUUID();
    AddDeliveryItemRequest body = new AddDeliveryItemRequest(goodsItemId, BigDecimal.valueOf(5));
    doNothing()
        .when(deliveryRequestService)
        .addItem(any(UUID.class), any(UUID.class), any(BigDecimal.class));

    mockMvc
        .perform(
            post("/api/v1/delivery/requests/{id}/items", requestId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk());

    verify(deliveryRequestService)
        .addItem(eq(requestId), eq(goodsItemId), eq(BigDecimal.valueOf(5)));
  }

  @Test
  @DisplayName(
      "POST /api/v1/delivery/requests/{id}/items returns 404 when delivery request is missing")
  void addItem_returns404_whenRequestMissing() throws Exception {
    UUID requestId = UUID.randomUUID();
    UUID goodsItemId = UUID.randomUUID();
    AddDeliveryItemRequest body = new AddDeliveryItemRequest(goodsItemId, BigDecimal.valueOf(5));
    doThrow(new EntityNotFoundException("DeliveryRequest", requestId))
        .when(deliveryRequestService)
        .addItem(any(UUID.class), any(UUID.class), any(BigDecimal.class));

    mockMvc
        .perform(
            post("/api/v1/delivery/requests/{id}/items", requestId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isNotFound());
  }
}
