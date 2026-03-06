package com.wms.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PlatformApplication {

  static void main(String[] args) {
    SpringApplication.run(PlatformApplication.class, args);
  }
}
