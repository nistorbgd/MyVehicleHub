package com.nst.myvehiclehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyVehicleHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyVehicleHubApplication.class, args);
    }

}
