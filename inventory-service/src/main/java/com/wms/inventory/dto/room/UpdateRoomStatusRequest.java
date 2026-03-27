package com.wms.inventory.dto.room;

import com.wms.common.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRoomStatusRequest(@NotNull RoomStatus status) {}
