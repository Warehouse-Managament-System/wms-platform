package com.wms.identity.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWarehouseOwnerRequest {

    private String firstName;
    private String lastName;

    private String companyName;
    private String taxId;
    private String address;
    private String city;
    private String country;
}
