package com.wms.identity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "wms.super-admin")
public class SuperAdminProperties {

  private String email;
  private String password;
  private String firstName = "Super";
  private String lastName = "Admin";
}
