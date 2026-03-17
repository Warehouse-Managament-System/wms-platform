package com.wms.identity.service;

import com.wms.common.dto.PageResponse;
import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.common.exception.EntityNotFoundException;
import com.wms.common.exception.ResourceConflictException;
import com.wms.identity.dto.customer.CreateCustomerRequest;
import com.wms.identity.dto.customer.CustomerResponse;
import com.wms.identity.dto.customer.UpdateCustomerRequest;
import com.wms.identity.entity.Customer;
import com.wms.identity.entity.User;
import com.wms.identity.repository.CustomerProfileRepository;
import com.wms.identity.repository.UserRepository;
import com.wms.identity.specification.CustomerSpecification;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerProfileRepository customerRepository;
  private final UserRepository userRepository;

  @Transactional
  public CustomerResponse create(CreateCustomerRequest request) {
    User user =
        userRepository
            .findById(request.userId())
            .orElseThrow(() -> new EntityNotFoundException("User", request.userId()));

    if (user.getRole() != UserRole.CUSTOMER) {
      throw new BusinessRuleException("User role must be CUSTOMER, but was " + user.getRole());
    }

    if (customerRepository.existsByUserId(request.userId())) {
      throw new ResourceConflictException(
          "Customer profile already exists for user: " + request.userId());
    }

    if (customerRepository.existsByTaxId(request.taxId())) {
      throw new ResourceConflictException("Tax ID already in use: " + request.taxId());
    }

    Customer customer =
        Customer.builder()
            .user(user)
            .companyName(request.companyName())
            .taxId(request.taxId())
            .contactPersonName(request.contactPersonName())
            .build();

    return CustomerResponse.from(customerRepository.save(customer));
  }

  @Transactional(readOnly = true)
  public CustomerResponse getById(UUID id) {
    return CustomerResponse.from(findByIdOrThrow(id));
  }

  @Transactional(readOnly = true)
  public CustomerResponse getByUserId(UUID userId) {
    return CustomerResponse.from(
        customerRepository
            .findByUserId(userId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Customer profile not found for user: " + userId)));
  }

  @Transactional(readOnly = true)
  public PageResponse<CustomerResponse> search(
      String search,
      UserStatus status,
      String taxId,
      Instant createdFrom,
      Instant createdTo,
      Pageable pageable) {

    Specification<Customer> spec = Specification.where((Specification<Customer>) null);

    if (search != null && !search.isBlank()) {
      spec = spec.and(CustomerSpecification.searchByName(search));
    }

    if (status != null) {
      spec = spec.and(CustomerSpecification.hasUserStatus(status));
    }

    if (taxId != null && !taxId.isBlank()) {
      spec = spec.and(CustomerSpecification.hasTaxId(taxId));
    }

    if (createdFrom != null) {
      spec = spec.and(CustomerSpecification.createdAfter(createdFrom));
    }

    if (createdTo != null) {
      spec = spec.and(CustomerSpecification.createdBefore(createdTo));
    }

    return PageResponse.from(
        customerRepository.findAll(spec, pageable).map(CustomerResponse::from));
  }

  @Transactional
  public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
    Customer customer = findByIdOrThrow(id);

    if (request.taxId() != null && !request.taxId().equals(customer.getTaxId())) {
      if (customerRepository.existsByTaxIdAndIdNot(request.taxId(), id)) {
        throw new ResourceConflictException("Tax ID already in use");
      }
      customer.setTaxId(request.taxId());
    }

    if (request.companyName() != null) customer.setCompanyName(request.companyName());

    if (request.contactPersonName() != null) {
      customer.setContactPersonName(request.contactPersonName());
    }

    return CustomerResponse.from(customerRepository.save(customer));
  }

  @Transactional
  public void delete(UUID id) {
    Customer customer = findByIdOrThrow(id);
    customer.getUser().setStatus(UserStatus.DEACTIVATED);
    customerRepository.delete(customer);
  }

  private Customer findByIdOrThrow(UUID id) {
    return customerRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Customer", id));
  }
}
