package com.lab4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.validation.annotation.Validated;

//http:/localhost:8080/api-docs - swagger
//  http:/localhost:8080/swagger-ui/index.html


@SpringBootApplication
public class PacientApp {

    public static void main(String[] args) {
        SpringApplication.run(PacientApp.class, args);
        System.out.println("----------------------");
    }
}
