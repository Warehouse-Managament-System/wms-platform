package com.wms.reservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI reservationServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("WMS Reservation Service API")
                .description(
                    "Booking, invoicing, and payment processing for the WMS Platform. Owns the"
                        + " booking lifecycle, price calculation, Stripe checkout integration, and"
                        + " Redis-backed concurrency locks for double-booking prevention.")
                .version("v1")
                .contact(new Contact().name("WMS Platform Team").email("team@wms.com"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
