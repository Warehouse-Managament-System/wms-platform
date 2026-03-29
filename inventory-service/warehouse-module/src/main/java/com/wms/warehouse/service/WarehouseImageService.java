package com.wms.warehouse.service;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.warehouse.dto.warehouse.AddWarehouseImageRequest;
import com.wms.warehouse.dto.warehouse.WarehouseImageResponse;
import com.wms.warehouse.entity.Warehouse;
import com.wms.warehouse.entity.WarehouseImage;
import com.wms.warehouse.repository.WarehouseImageRepository;
import com.wms.warehouse.repository.WarehouseRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WarehouseImageService {

  private final WarehouseImageRepository imageRepository;
  private final WarehouseRepository warehouseRepository;

  @Transactional
  public WarehouseImageResponse addImage(
      UUID warehouseId, UUID ownerId, AddWarehouseImageRequest request) {
    Warehouse warehouse = findWarehouseOrThrow(warehouseId);
    verifyOwnership(warehouse, ownerId);

    if (request.isPrimary() && imageRepository.existsByWarehouseIdAndIsPrimaryTrue(warehouseId)) {
      imageRepository.findByWarehouseId(warehouseId).stream()
          .filter(WarehouseImage::getIsPrimary)
          .forEach(
              image -> {
                image.setIsPrimary(false);
                imageRepository.save(image);
              });
    }

    WarehouseImage image =
        WarehouseImage.builder()
            .warehouse(warehouse)
            .url(request.url())
            .isPrimary(request.isPrimary())
            .build();

    return WarehouseImageResponse.from(imageRepository.save(image));
  }

  @Transactional
  public void deleteImage(UUID warehouseId, UUID imageId, UUID ownerId) {
    Warehouse warehouse = findWarehouseOrThrow(warehouseId);
    verifyOwnership(warehouse, ownerId);

    if (!imageRepository.existsById(imageId)) {
      throw new EntityNotFoundException("WarehouseImage", imageId);
    }

    imageRepository.deleteByWarehouseIdAndId(warehouseId, imageId);
  }

  private Warehouse findWarehouseOrThrow(UUID warehouseId) {
    return warehouseRepository
        .findById(warehouseId)
        .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
  }

  private void verifyOwnership(Warehouse warehouse, UUID ownerId) {
    if (!warehouse.getOwnerId().equals(ownerId)) {
      throw new EntityNotFoundException("Warehouse", warehouse.getId());
    }
  }
}
