package com.wms.reservation;

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
      "com.wms.reservation",
      "com.wms.common.exception",
      "com.wms.common.security",
      "com.wms.common.outbox"
    })
@EntityScan(basePackages = {"com.wms.reservation.entity", "com.wms.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wms.reservation.repository", "com.wms.common.outbox"})
public class ReservationApplication {

  static void main(String[] args) {
    SpringApplication.run(ReservationApplication.class, args);
  }
}
