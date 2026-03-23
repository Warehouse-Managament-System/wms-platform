package com.wms.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(
    basePackages = {"com.wms.inventory", "com.wms.common.exception", "com.wms.common.security"})
@EntityScan(basePackages = {"com.wms.inventory.entity", "com.wms.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wms.inventory.repository"})
public class InventoryApplication {

  static void main(String[] args) {
    SpringApplication.run(InventoryApplication.class, args);
  }
}
