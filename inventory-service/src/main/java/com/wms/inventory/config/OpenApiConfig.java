package com.wms.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI inventoryServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("WMS Inventory Service API")
                .description(
                    "Warehouse and goods management for the WMS Platform. Owns warehouses, zones,"
                        + " rooms, categories, goods imports, and goods receipts. Includes Excel"
                        + " bulk-import endpoints for warehouse and goods onboarding.")
                .version("v1")
                .contact(new Contact().name("WMS Platform Team").email("team@wms.com"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
