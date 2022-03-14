package com.example.democoin;

import com.example.democoin.configuration.properties.SlackProperties;
import com.example.democoin.configuration.properties.UpbitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.TimeZone;

@EnableScheduling
@EnableConfigurationProperties({UpbitProperties.class, SlackProperties.class})
@SpringBootApplication
public class DemoCoinApplication {

    public static DecimalFormat df = new DecimalFormat("###,###"); // 출력 숫자 포맷

    public static void main(String[] args) {
        SpringApplication.run(DemoCoinApplication.class, args);
    }

    @PostConstruct
    private void postConstruct() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}