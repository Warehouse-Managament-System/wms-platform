package com.wms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @Email(message = "Duzgun email daxil edin")
    @NotBlank(message = "Email bos ola bilmez")
    private String email;
    @NotBlank(message = "sifre bos ola bilmez")
    private String password;


}
