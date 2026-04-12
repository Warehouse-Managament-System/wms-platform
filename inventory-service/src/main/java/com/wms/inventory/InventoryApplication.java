package com.wms.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.wms.inventory", "com.wms.warehouse", "com.wms.goods"})
@EnableScheduling
@ComponentScan(
    basePackages = {
      "com.wms.inventory",
      "com.wms.warehouse",
      "com.wms.goods",
      "com.wms.common.exception",
      "com.wms.common.security",
      "com.wms.common.outbox"
    })
@EntityScan(
    basePackages = {
      "com.wms.inventory.entity",
      "com.wms.warehouse.entity",
      "com.wms.goods.entity",
      "com.wms.common.entity"
    })
@EnableJpaRepositories(
    basePackages = {
      "com.wms.inventory.repository",
      "com.wms.warehouse.repository",
      "com.wms.goods.repository",
      "com.wms.common.outbox"
    })
public class InventoryApplication {
  static void main(String[] args) {
    SpringApplication.run(InventoryApplication.class, args);
  }
}
