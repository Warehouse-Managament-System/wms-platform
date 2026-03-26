package com.wms.inventory.dto.category;

import com.wms.inventory.entity.Category;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(UUID id, String name, String description, Instant createdAt) {
  public static CategoryResponse from(Category category) {
    return new CategoryResponse(
        category.getId(), category.getName(), category.getDescription(), category.getCreatedAt());
  }
}
