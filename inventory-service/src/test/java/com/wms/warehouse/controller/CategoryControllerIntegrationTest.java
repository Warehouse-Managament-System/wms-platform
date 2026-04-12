package com.wms.warehouse.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.exception.GlobalExceptionHandler;
import com.wms.common.exception.ResourceConflictException;
import com.wms.warehouse.dto.category.CategoryResponse;
import com.wms.warehouse.dto.category.CreateCategoryRequest;
import com.wms.warehouse.service.CategoryService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("CategoryController integration test")
class CategoryControllerIntegrationTest {

  private MockMvc mockMvc;
  private CategoryService categoryService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    categoryService = mock(CategoryService.class);
    CategoryController controller = new CategoryController(categoryService);
    mockMvc =
        standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
  }

  @Test
  @DisplayName("POST /api/v1/categories creates a category and returns 201 with body")
  void createCategory_returns201() throws Exception {
    CreateCategoryRequest request = new CreateCategoryRequest("Electronics", "Consumer electronics");
    UUID generatedId = UUID.randomUUID();
    CategoryResponse created =
        new CategoryResponse(generatedId, "Electronics", "Consumer electronics", Instant.now());
    when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(created);

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(generatedId.toString()))
        .andExpect(jsonPath("$.name").value("Electronics"))
        .andExpect(jsonPath("$.description").value("Consumer electronics"));
  }

  @Test
  @DisplayName("POST /api/v1/categories returns 409 when the category name already exists")
  void createCategory_returns409_whenDuplicate() throws Exception {
    CreateCategoryRequest request = new CreateCategoryRequest("Electronics", "Consumer electronics");
    when(categoryService.create(any(CreateCategoryRequest.class)))
        .thenThrow(new ResourceConflictException("Category name already exists!"));

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("GET /api/v1/categories/{id} returns the category as JSON")
  void getById_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    CategoryResponse response =
        new CategoryResponse(id, "Electronics", "Consumer electronics", Instant.now());
    when(categoryService.getById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/categories/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Electronics"));
  }
}
