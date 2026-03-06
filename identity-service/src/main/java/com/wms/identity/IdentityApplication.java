package com.wms.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IdentityApplication {

  static void main(String[] args) {
    SpringApplication.run(IdentityApplication.class, args);
  }
}
