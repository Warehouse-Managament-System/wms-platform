package com.wms.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@ComponentScan(
    basePackages = {
      "com.wms.identity",
      "com.wms.common.exception",
      "com.wms.common.security",
      "com.wms.common.outbox"
    })
@EntityScan(basePackages = {"com.wms.identity.entity", "com.wms.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wms.identity.repository", "com.wms.common.outbox"})
public class IdentityApplication {

  static void main(String[] args) {
    SpringApplication.run(IdentityApplication.class, args);
  }
}
