package com.wms.inventory.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.WarehouseStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.inventory.dto.warehouse.CreateWarehouseRequest;
import com.wms.inventory.dto.warehouse.UpdateWarehouseRequest;
import com.wms.inventory.dto.warehouse.WarehouseResponse;
import com.wms.inventory.entity.Warehouse;
import com.wms.inventory.repository.WarehouseRepository;
import com.wms.inventory.specification.WarehouseSpecification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WarehouseService {

  private final WarehouseRepository warehouseRepository;

  @Transactional
  public WarehouseResponse create(UUID ownerId, CreateWarehouseRequest request) {
    Warehouse warehouse =
        Warehouse.builder()
            .ownerId(ownerId)
            .name(request.name())
            .description(request.description())
            .address(request.address())
            .city(request.city())
            .country(request.country())
            .latitude(request.latitude())
            .longitude(request.longitude())
            .totalSurfaceArea(request.totalSurfaceArea())
            .discountPercentage(
                request.discountPercentage() != null ? request.discountPercentage() : 0)
            .status(WarehouseStatus.DRAFT)
            .build();

    return WarehouseResponse.from(warehouseRepository.save(warehouse));
  }

  @Transactional(readOnly = true)
  public WarehouseResponse getById(UUID id) {
    Warehouse warehouse = findOrThrow(id);
    return WarehouseResponse.from(warehouse);
  }

  @Transactional(readOnly = true)
  public PageResponse<WarehouseResponse> searchOwn(
      UUID ownerId,
      String search,
      WarehouseStatus status,
      Instant createdFrom,
      Instant createdTo,
      Pageable pageable) {

    Specification<Warehouse> spec = WarehouseSpecification.hasOwnerId(ownerId);

    if (search != null && !search.isBlank()) {
      spec = spec.and(WarehouseSpecification.searchByNameOrCity(search));
    }

    if (status != null) {
      spec = spec.and(WarehouseSpecification.hasStatus(status));
    }

    return getWarehouseResponsePageResponse(createdFrom, createdTo, pageable, spec);
  }

  @Transactional(readOnly = true)
  public PageResponse<WarehouseResponse> searchPublished(
      String search,
      String city,
      String country,
      Instant createdFrom,
      Instant createdTo,
      Pageable pageable) {

    Specification<Warehouse> spec = WarehouseSpecification.hasStatus(WarehouseStatus.PUBLISHED);

    if (search != null && !search.isBlank()) {
      spec = spec.and(WarehouseSpecification.searchByNameOrCity(search));
    }

    if (city != null && !city.isBlank()) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
    }

    if (country != null && !country.isBlank()) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase() + "%"));
    }

    return getWarehouseResponsePageResponse(createdFrom, createdTo, pageable, spec);
  }

  @NonNull
  private PageResponse<WarehouseResponse> getWarehouseResponsePageResponse(
      Instant createdFrom, Instant createdTo, Pageable pageable, Specification<Warehouse> spec) {
    if (createdFrom != null) {
      spec = spec.and(WarehouseSpecification.createdAfter(createdFrom));
    }

    if (createdTo != null) {
      spec = spec.and(WarehouseSpecification.createdBefore(createdTo));
    }

    return PageResponse.from(
        warehouseRepository.findAll(spec, pageable).map(WarehouseResponse::from));
  }

  @Transactional
  public WarehouseResponse update(UUID id, UUID ownerId, UpdateWarehouseRequest request) {
    Warehouse warehouse = findOrThrow(id);
    verifyOwnership(warehouse, ownerId);

    if (warehouse.getStatus() != WarehouseStatus.DRAFT
        && warehouse.getStatus() != WarehouseStatus.PUBLISHED) {
      throw new BusinessRuleException("Warehouse can only be updated in DRAFT or PUBLISHED status");
    }

    if (request.name() != null) warehouse.setName(request.name());

    if (request.description() != null) warehouse.setDescription(request.description());

    if (request.address() != null) warehouse.setAddress(request.address());

    if (request.city() != null) warehouse.setCity(request.city());

    if (request.country() != null) warehouse.setCountry(request.country());

    if (request.latitude() != null) warehouse.setLatitude(request.latitude());

    if (request.longitude() != null) warehouse.setLongitude(request.longitude());

    if (request.totalSurfaceArea() != null) {
      warehouse.setTotalSurfaceArea(request.totalSurfaceArea());
    }

    if (request.discountPercentage() != null) {
      warehouse.setDiscountPercentage(request.discountPercentage());
    }

    return WarehouseResponse.from(warehouseRepository.save(warehouse));
  }

  @Transactional
  public WarehouseResponse publish(UUID id, UUID ownerId) {
    Warehouse warehouse = findOrThrow(id);
    verifyOwnership(warehouse, ownerId);

    if (warehouse.getStatus() != WarehouseStatus.DRAFT) {
      throw new BusinessRuleException("Only DRAFT warehouses can be published");
    }

    warehouse.setStatus(WarehouseStatus.PUBLISHED);
    return WarehouseResponse.from(warehouseRepository.save(warehouse));
  }

  private Warehouse findOrThrow(UUID id) {
    return warehouseRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Warehouse", id));
  }

  private void verifyOwnership(Warehouse warehouse, UUID ownerId) {
    if (!warehouse.getOwnerId().equals(ownerId)) {
      throw new EntityNotFoundException("Warehouse", warehouse.getId());
    }
  }
}
