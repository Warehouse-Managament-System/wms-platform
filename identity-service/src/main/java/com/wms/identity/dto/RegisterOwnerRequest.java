package com.wms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterOwnerRequest {

    @Email(message = "Email düzgün formatda olmalıdır")
    @NotBlank(message = "Email boş ola bilməz")
    private String email;

    @NotBlank(message = "Password boş ola bilməz")
    @Size(min = 6, message = "Password ən azı 6 simvol olmalıdır")
    private String password;

    @NotBlank(message = "Ad boş ola bilməz")
    private String firstName;

    @NotBlank(message = "Soyad boş ola bilməz")
    private String lastName;

    @NotBlank(message = "Telefon nömrəsi boş ola bilməz")
    private String phone;

    @NotBlank(message = "Şirkət adı boş ola bilməz")
    private String companyName;

    @NotBlank(message = "Tax ID boş ola bilməz")
    private String taxId;

    @NotBlank(message = "Address boş ola bilməz")
    private String address;
}
