package com.miniforce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniForceApplication {
  public static void main(String[] args) { SpringApplication.run(MiniForceApplication.class, args); }
}
