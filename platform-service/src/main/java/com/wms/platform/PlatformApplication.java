package com.wms.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@ComponentScan(
    basePackages = {
      "com.wms.platform",
      "com.wms.common.exception",
      "com.wms.common.outbox",
      "com.wms.common.security"
    })
@EntityScan(basePackages = {"com.wms.platform.entity", "com.wms.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wms.platform.repository", "com.wms.common.outbox"})
public class PlatformApplication {

  static void main(String[] args) {
    SpringApplication.run(PlatformApplication.class, args);
  }
}
