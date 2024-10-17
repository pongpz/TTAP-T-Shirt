package com.project.ttaptshirt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Bật tính năng lập lịch
public class TtapTShirtApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtapTShirtApplication.class, args);
    }

}
