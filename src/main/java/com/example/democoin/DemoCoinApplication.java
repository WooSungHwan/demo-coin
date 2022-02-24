package com.example.democoin;

import com.example.democoin.configuration.properties.UpbitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties({UpbitProperties.class})
@SpringBootApplication
public class DemoCoinApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCoinApplication.class, args);
    }
}