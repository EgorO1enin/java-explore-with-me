package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("ru.practicum.ewm.model")
public class EwmMainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EwmMainServiceApplication.class, args);
    }
}
