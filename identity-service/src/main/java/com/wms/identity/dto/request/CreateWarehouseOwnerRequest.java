package com.wms.identity.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWarehouseOwnerRequest {

    // USER
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    // PROFILE
    private String companyName;
    private String taxId;
    private String address;
    private String city;
    private String country;
}
