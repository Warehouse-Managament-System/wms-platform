package com.wms.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wms.common.enums.DeliveryStatus;
import com.wms.common.exception.BusinessRuleException;
import com.wms.delivery.dto.delivery.DeliveryRequestItemResponse;
import com.wms.delivery.dto.delivery.PickItemRequest;
import com.wms.delivery.entity.DeliveryRequest;
import com.wms.delivery.entity.DeliveryRequestItem;
import com.wms.delivery.repository.DeliveryRequestItemRepository;
import com.wms.delivery.repository.DeliveryRequestRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryRequestItemService unit tests")
class DeliveryRequestItemServiceTest {

  @Mock private DeliveryRequestItemRepository itemRepository;
  @Mock private DeliveryRequestRepository deliveryRequestRepository;

  @InjectMocks private DeliveryRequestItemService service;

  private UUID deliveryRequestId;
  private UUID goodsItemId;
  private UUID staffId;
  private DeliveryRequest deliveryRequest;
  private DeliveryRequestItem item;

  @BeforeEach
  void setUp() {
    deliveryRequestId = UUID.randomUUID();
    goodsItemId = UUID.randomUUID();
    staffId = UUID.randomUUID();

    deliveryRequest = DeliveryRequest.builder().status(DeliveryStatus.PICKING).build();
    deliveryRequest.setId(deliveryRequestId);

    item =
        DeliveryRequestItem.builder()
            .deliveryRequest(deliveryRequest)
            .goodsItemId(goodsItemId)
            .requestedQty(BigDecimal.valueOf(10))
            .pickedQty(BigDecimal.ZERO)
            .build();
    item.setId(UUID.randomUUID());
  }

  @Test
  @DisplayName("pickItem updates picked qty and saves the item when within requested quantity")
  void pickItem_updatesAndSaves_whenValid() {
    when(deliveryRequestRepository.findById(deliveryRequestId))
        .thenReturn(Optional.of(deliveryRequest));
    when(itemRepository.findByDeliveryRequestIdAndGoodsItemId(deliveryRequestId, goodsItemId))
        .thenReturn(Optional.of(item));

    PickItemRequest request = new PickItemRequest(goodsItemId, BigDecimal.valueOf(4));
    DeliveryRequestItemResponse response = service.pickItem(deliveryRequestId, staffId, request);

    assertThat(response.pickedQty()).isEqualByComparingTo("4");
    assertThat(response.pickedBy()).isEqualTo(staffId);
    assertThat(item.getPickedAt()).isNotNull();
    verify(itemRepository).save(item);
  }

  @Test
  @DisplayName("pickItem rejects with BusinessRuleException when delivery is not in PICKING status")
  void pickItem_throws_whenStatusNotPicking() {
    deliveryRequest.setStatus(DeliveryStatus.PENDING);
    when(deliveryRequestRepository.findById(deliveryRequestId))
        .thenReturn(Optional.of(deliveryRequest));

    PickItemRequest request = new PickItemRequest(goodsItemId, BigDecimal.valueOf(4));

    assertThatThrownBy(() -> service.pickItem(deliveryRequestId, staffId, request))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("PICKING");

    verify(itemRepository, never()).save(item);
  }

  @Test
  @DisplayName("pickItem rejects when picked quantity would exceed the requested quantity")
  void pickItem_throws_whenExceedingRequestedQty() {
    item.setPickedQty(BigDecimal.valueOf(8));
    when(deliveryRequestRepository.findById(deliveryRequestId))
        .thenReturn(Optional.of(deliveryRequest));
    when(itemRepository.findByDeliveryRequestIdAndGoodsItemId(deliveryRequestId, goodsItemId))
        .thenReturn(Optional.of(item));

    PickItemRequest request = new PickItemRequest(goodsItemId, BigDecimal.valueOf(5));

    assertThatThrownBy(() -> service.pickItem(deliveryRequestId, staffId, request))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("exceeds");

    verify(itemRepository, never()).save(item);
  }
}
