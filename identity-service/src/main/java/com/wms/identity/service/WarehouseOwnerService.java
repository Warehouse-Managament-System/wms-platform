package com.wms.identity.service;

import com.wms.common.enums.UserRole;
import com.wms.identity.dto.request.CreateWarehouseOwnerRequest;
import com.wms.identity.dto.request.UpdateWarehouseOwnerRequest;
import com.wms.identity.dto.response.WarehouseOwnerResponse;
import com.wms.identity.entity.User;
import com.wms.identity.entity.WarehouseOwner;
import com.wms.identity.exception.AlreadyExistsException;
import com.wms.identity.exception.NotFoundException;
import com.wms.identity.mapper.WarehouseOwnerMapper;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.repository.WarehouseOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseOwnerService {

    private final WarehouseOwnerRepository repository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final WarehouseOwnerMapper mapper;

    @Transactional
    public void create(CreateWarehouseOwnerRequest request) {

        if (repository.existsByTaxIdAndDeletedAtIsNull(request.getTaxId())) {
            throw new AlreadyExistsException("Tax ID already exists");
        }

        User user = userService.createUser(
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            UserRole.WAREHOUSE_OWNER
        );

        WarehouseOwner owner = WarehouseOwner.builder()
            .user(user)
            .companyName(request.getCompanyName())
            .taxId(request.getTaxId())
            .address(request.getAddress())
            .city(request.getCity())
            .country(request.getCountry())
            .build();

        repository.save(owner);
    }

    public WarehouseOwnerResponse get(UUID id) {

        WarehouseOwner owner = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Warehouse owner not found"));
        return mapper.toResponse(owner);
    }

    public List<WarehouseOwnerResponse> getAll() {

        return repository.findAllActive()
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

//    @Transactional
//    public void approve(UUID ownerId, UUID adminId) {
//
//        WarehouseOwner owner = repository.findActiveById(ownerId)
//            .orElseThrow(() -> new NotFoundException("Warehouse owner not found"));
//
//        User admin = userRepository.findById(adminId)
//            .orElseThrow(() -> new NotFoundException("Admin not found"));
//
//        owner.setApprovedBy(admin);
//        owner.setApprovedAt(Instant.now());
//        owner.setRejectionReason(null);
//    }
//
//    @Transactional
//    public void reject(UUID ownerId, UUID adminId, String reason) {
//
//        WarehouseOwner owner = repository.findActiveById(ownerId)
//            .orElseThrow(() -> new NotFoundException("Warehouse owner not found"));
//
//        User admin = userRepository.findById(adminId)
//            .orElseThrow(() -> new RuntimeException("Admin not found"));
//
//        owner.setApprovedBy(admin);
//        owner.setRejectionReason(reason);
//        owner.setApprovedAt(null);
//    }

    @Transactional
    public void update(UUID id, UpdateWarehouseOwnerRequest request) {

        WarehouseOwner owner = repository.findActiveById(id)
            .orElseThrow(() -> new NotFoundException("Warehouse owner not found"));
        User user = owner.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        owner.setCompanyName(request.getCompanyName());
        owner.setTaxId(request.getTaxId());
        owner.setAddress(request.getAddress());
        owner.setCity(request.getCity());
        owner.setCountry(request.getCountry());
    }

    @Transactional
    public void delete(UUID id) {

        WarehouseOwner owner = repository.findActiveById(id)
            .orElseThrow(() -> new NotFoundException("Warehouse owner not found"));
        Instant now = Instant.now();

        owner.setDeletedAt(now);
        owner.getUser().setDeletedAt(now);
    }
}
