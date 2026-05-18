package com.example.vespa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VespaApplication {
    public static void main(String[] args) {
        SpringApplication.run(VespaApplication.class, args);
    }
}
