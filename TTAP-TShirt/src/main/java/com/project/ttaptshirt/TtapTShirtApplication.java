package com.project.ttaptshirt;

import com.project.ttaptshirt.controller.MaGiamGiaController;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
