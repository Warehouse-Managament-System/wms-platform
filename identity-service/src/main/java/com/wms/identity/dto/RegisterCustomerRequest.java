package com.wms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCustomerRequest {

    @Email(message = "Email düzgün formatda olmalıdır")
    @NotBlank(message = "Email boş ola bilməz")
    private String email;

    @NotBlank(message = "Password boş ola bilməz")
    @Size(min = 6, message = "Password ən azı 6 simvol olmalıdır")
    private String password;

    @NotBlank(message = "Company name boş ola bilməz")
    private String companyName;

    @NotBlank(message = "Tax ID boş ola bilməz")
    private String taxId;

    @NotBlank(message = "Contact person name boş ola bilməz")
    private String contactPersonName;
}
