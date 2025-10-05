package com.qmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QMateApplication {

  public static void main(String[] args) {
    SpringApplication.run(QMateApplication.class, args);
  }

}
