package com.wms.warehouse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.warehouse.dto.category.CategoryResponse;
import com.wms.warehouse.dto.category.CreateCategoryRequest;
import com.wms.warehouse.entity.Category;
import com.wms.warehouse.repository.CategoryRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService unit tests")
class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private CategoryService categoryService;

  @Test
  @DisplayName("create persists a new category and returns the response when the name is unique")
  void create_persistsAndReturns_whenNameIsUnique() {
    CreateCategoryRequest request = new CreateCategoryRequest("Electronics", "Consumer electronics");
    when(categoryRepository.existsByName("Electronics")).thenReturn(false);
    when(categoryRepository.save(any(Category.class)))
        .thenAnswer(
            inv -> {
              Category c = inv.getArgument(0);
              c.setId(UUID.randomUUID());
              c.setCreatedAt(Instant.now());
              return c;
            });

    CategoryResponse response = categoryService.create(request);

    assertThat(response.name()).isEqualTo("Electronics");
    assertThat(response.description()).isEqualTo("Consumer electronics");
    assertThat(response.id()).isNotNull();
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  @DisplayName("create throws ResourceConflictException when a category with the same name exists")
  void create_throwsConflict_whenNameAlreadyExists() {
    CreateCategoryRequest request = new CreateCategoryRequest("Electronics", "Consumer electronics");
    when(categoryRepository.existsByName("Electronics")).thenReturn(true);

    assertThatThrownBy(() -> categoryService.create(request))
        .isInstanceOf(ResourceConflictException.class)
        .hasMessageContaining("already exists");

    verify(categoryRepository, never()).save(any(Category.class));
  }

  @Test
  @DisplayName("getById throws EntityNotFoundException when the category is not found")
  void getById_throws_whenCategoryNotFound() {
    UUID missingId = UUID.randomUUID();
    when(categoryRepository.findById(missingId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.getById(missingId))
        .isInstanceOf(EntityNotFoundException.class);
  }
}
