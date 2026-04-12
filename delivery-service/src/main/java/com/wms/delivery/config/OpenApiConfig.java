package com.wms.delivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI deliveryServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("WMS Delivery Service API")
                .description(
                    "Delivery and shipment management for the WMS Platform. Owns delivery"
                        + " requests, item picking by staff, agent claims, and live shipment"
                        + " tracking via checkpoints.")
                .version("v1")
                .contact(new Contact().name("WMS Platform Team").email("team@wms.com"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
