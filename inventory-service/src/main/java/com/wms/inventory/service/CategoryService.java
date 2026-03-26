package com.wms.inventory.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.inventory.dto.category.CategoryResponse;
import com.wms.inventory.dto.category.CreateCategoryRequest;
import com.wms.inventory.entity.Category;
import com.wms.inventory.repository.CategoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  @Transactional
  public CategoryResponse create(CreateCategoryRequest createCategoryRequest) {
    if (categoryRepository.existsByName(createCategoryRequest.name())) {
      throw new ResourceConflictException("Category name already exists!");
    }
    Category category =
        Category.builder()
            .name(createCategoryRequest.name())
            .description(createCategoryRequest.description())
            .build();
    return CategoryResponse.from(categoryRepository.save(category));
  }

  @Transactional(readOnly = true)
  public CategoryResponse getById(UUID id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found!", id));

    return CategoryResponse.from(category);
  }

  @Transactional(readOnly = true)
  public PageResponse<CategoryResponse> search(String search, Pageable pageable) {
    Specification<Category> spec = ((root, query, cb) -> cb.conjunction());

    if (search != null && !search.isBlank()) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
    }

    return PageResponse.from(
        categoryRepository.findAll(spec, pageable).map(CategoryResponse::from));
  }
}
