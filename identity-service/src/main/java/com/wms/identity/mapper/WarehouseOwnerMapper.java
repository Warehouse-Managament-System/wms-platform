package com.wms.identity.mapper;

import com.wms.identity.dto.response.WarehouseOwnerResponse;
import com.wms.identity.entity.User;
import com.wms.identity.entity.WarehouseOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseOwnerMapper {

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "approvedBy", target = "approvedBy") // User -> String mapping
    WarehouseOwnerResponse toResponse(WarehouseOwner entity);

    // Helper: User -> String (məsələn, firstName + lastName)
    default String map(User user) {
        if (user == null) return null;
        return user.getFirstName() + " " + user.getLastName();
    }
}
