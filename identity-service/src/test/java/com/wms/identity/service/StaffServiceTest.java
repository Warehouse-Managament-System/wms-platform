package com.wms.identity.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wms.common.exception.EntityNotFoundException;
import com.wms.identity.dto.staff.CreateStaffRequest;
import com.wms.identity.entity.Staff;
import com.wms.identity.feign.WarehouseClient;
import com.wms.identity.repository.StaffRepository;
import com.wms.identity.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffService warehouse-validation tests")
class StaffServiceTest {

  @Mock private StaffRepository staffRepository;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private WarehouseClient warehouseClient;

  @InjectMocks private StaffService staffService;

  @Test
  @DisplayName("create rejects with EntityNotFoundException when warehouseId does not exist")
  void create_rejectsPhantomWarehouseId() {
    UUID phantomWarehouse = UUID.randomUUID();
    CreateStaffRequest request = createStaffRequest(phantomWarehouse);

    when(userRepository.existsByEmailAndDeletedAtIsNull("alice@example.com")).thenReturn(false);
    doThrow(new RuntimeException("404 Not Found"))
        .when(warehouseClient)
        .verifyExists(phantomWarehouse);

    assertThatThrownBy(() -> staffService.create(request))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(phantomWarehouse.toString());

    verify(userRepository, never()).save(any());
    verify(staffRepository, never()).save(any(Staff.class));
  }

  @Test
  @DisplayName("create succeeds and persists staff when warehouseId is valid")
  void create_succeeds_whenWarehouseIsValid() {
    UUID realWarehouse = UUID.randomUUID();
    CreateStaffRequest request = createStaffRequest(realWarehouse);

    when(userRepository.existsByEmailAndDeletedAtIsNull("alice@example.com")).thenReturn(false);
    doNothing().when(warehouseClient).verifyExists(realWarehouse);
    when(passwordEncoder.encode("Password1!")).thenReturn("hashed");
    when(staffRepository.save(any(Staff.class))).thenAnswer(inv -> inv.getArgument(0));

    staffService.create(request);

    verify(warehouseClient).verifyExists(realWarehouse);
    verify(staffRepository).save(any(Staff.class));
  }

  private static CreateStaffRequest createStaffRequest(UUID warehouseId) {
    return new CreateStaffRequest(
        "alice@example.com", "Password1!", "Alice", "Anderson", warehouseId, "Manager");
  }
}
