package com.danish.academicsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AcademicSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicSyncApplication.class, args);
    }
}