package com.wms.identity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI identityServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("WMS Identity Service API")
                .description(
                    "Authentication, user management, and role-based access control for the WMS"
                        + " Platform. Owns users, profiles, JWT issuing, and the warehouse owner"
                        + " approval workflow.")
                .version("v1")
                .contact(new Contact().name("WMS Platform Team").email("team@wms.com"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
