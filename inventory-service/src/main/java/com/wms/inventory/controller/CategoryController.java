package com.wms.inventory.controller;

import com.wms.common.dto.PageResponse;
import com.wms.inventory.dto.category.CategoryResponse;
import com.wms.inventory.dto.category.CreateCategoryRequest;
import com.wms.inventory.service.CategoryService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;
  private static final Set<String> ALLOWED_SORT_FIELD = Set.of("createdAt", "name");

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
      @Valid @RequestBody CreateCategoryRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(categoryService.getById(id));
  }

  @GetMapping
  public ResponseEntity<PageResponse<CategoryResponse>> search(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection) {
    if (!ALLOWED_SORT_FIELD.contains(sortBy)) {
      sortBy = "createdAt";
    }
    Sort sort =
        sortDirection.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    PageRequest pageable = PageRequest.of(page, Math.min(size, 100), sort);
    return ResponseEntity.ok(categoryService.search(search, pageable));
  }
}
