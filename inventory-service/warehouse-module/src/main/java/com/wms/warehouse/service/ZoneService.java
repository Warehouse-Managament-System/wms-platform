package com.wms.warehouse.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.TemperatureType;
import com.wms.common.enums.ZoneStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.warehouse.dto.zone.CreateZoneRequest;
import com.wms.warehouse.dto.zone.UpdateZoneRequest;
import com.wms.warehouse.dto.zone.ZoneAvailabilityRequest;
import com.wms.warehouse.dto.zone.ZoneAvailabilityResponse;
import com.wms.warehouse.dto.zone.ZoneResponse;
import com.wms.warehouse.entity.Category;
import com.wms.warehouse.entity.Warehouse;
import com.wms.warehouse.entity.Zone;
import com.wms.warehouse.entity.ZoneAvailability;
import com.wms.warehouse.entity.ZoneCategory;
import com.wms.warehouse.repository.CategoryRepository;
import com.wms.warehouse.repository.WarehouseRepository;
import com.wms.warehouse.repository.ZoneAvailabilityRepository;
import com.wms.warehouse.repository.ZoneCategoryRepository;
import com.wms.warehouse.repository.ZoneRepository;
import com.wms.warehouse.specification.ZoneSpecification;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ZoneService {
  private final ZoneRepository zoneRepository;
  private final WarehouseRepository warehouseRepository;
  private final ZoneCategoryRepository zoneCategoryRepository;
  private final ZoneAvailabilityRepository zoneAvailabilityRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  public ZoneResponse create(UUID warehouseId, UUID ownerId, CreateZoneRequest request) {
    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));

    if (!warehouse.getOwnerId().equals(ownerId)) {
      throw new BusinessRuleException("You do not own this warehouse");
    }

    Zone zone =
        Zone.builder()
            .warehouse(warehouse)
            .name(request.name())
            .description(request.description())
            .temperatureType(request.temperatureType())
            .totalSurfaceArea(request.totalSurfaceArea())
            .discountPercentage(
                request.discountPercentage() != null ? request.discountPercentage() : 0)
            .status(ZoneStatus.ACTIVE)
            .build();

    return ZoneResponse.from(zoneRepository.save(zone));
  }

  @Transactional(readOnly = true)
  public PageResponse<ZoneResponse> listByWarehouse(
      UUID warehouseId, TemperatureType tempType, ZoneStatus status, Pageable pageable) {
    Specification<Zone> spec = ZoneSpecification.hasWarehouseId(warehouseId);

    if (tempType != null) {
      spec = spec.and(ZoneSpecification.hasTemperatureType(tempType));
    }
    if (status != null) {
      spec = spec.and(ZoneSpecification.hasStatus(status));
    }

    return PageResponse.from(zoneRepository.findAll(spec, pageable).map(ZoneResponse::from));
  }

  @Transactional
  public void addCategory(UUID zoneId, UUID ownerId, UUID categoryId) {
    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new EntityNotFoundException("Zone", zoneId));

    if (!zone.getWarehouse().getOwnerId().equals(ownerId)) {
      throw new EntityNotFoundException("Zone", zoneId);
    }

    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));

    if (zoneCategoryRepository.existsByZoneIdAndCategoryId(zoneId, categoryId)) {
      throw new ResourceConflictException("Category already linked to this zone");
    }

    ZoneCategory zoneCategory = ZoneCategory.builder().zone(zone).category(category).build();

    zoneCategoryRepository.save(zoneCategory);
  }

  @Transactional
  public ZoneAvailabilityResponse addAvailability(
      UUID zoneId, UUID ownerId, ZoneAvailabilityRequest request) {
    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new EntityNotFoundException("Zone", zoneId));

    if (!zone.getWarehouse().getOwnerId().equals(ownerId)) {
      throw new BusinessRuleException("You do not own this zone");
    }

    if (!request.endDate().isAfter(request.startDate())) {
      throw new BusinessRuleException("End date must be after start date");
    }

    ZoneAvailability availability =
        ZoneAvailability.builder()
            .zone(zone)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .build();

    return ZoneAvailabilityResponse.from(zoneAvailabilityRepository.save(availability));
  }

  @Transactional
  public ZoneResponse update(UUID zoneId, UUID ownerId, UpdateZoneRequest request) {
    Zone zone =
        zoneRepository
            .findById(zoneId)
            .orElseThrow(() -> new EntityNotFoundException("Zone", zoneId));

    if (!zone.getWarehouse().getOwnerId().equals(ownerId)) {
      throw new BusinessRuleException("You do not own this zone");
    }

    if (request.name() != null) zone.setName(request.name());
    if (request.description() != null) zone.setDescription(request.description());
    if (request.temperatureType() != null) zone.setTemperatureType(request.temperatureType());
    if (request.totalSurfaceArea() != null) zone.setTotalSurfaceArea(request.totalSurfaceArea());
    if (request.discountPercentage() != null)
      zone.setDiscountPercentage(request.discountPercentage());
    if (request.status() != null) zone.setStatus(request.status());

    return ZoneResponse.from(zoneRepository.save(zone));
  }
}
