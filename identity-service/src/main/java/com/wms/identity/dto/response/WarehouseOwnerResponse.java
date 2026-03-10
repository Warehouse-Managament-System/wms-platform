package com.wms.identity.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseOwnerResponse {

    private String id;

    private String email;
    private String firstName;
    private String lastName;

    private String companyName;
    private String taxId;
    private String address;
    private String city;
    private String country;

    private String approvedBy;
    private String rejectionReason;
}
