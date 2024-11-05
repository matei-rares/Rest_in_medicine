package com.mongou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class MongouApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongouApplication.class, args);
        System.out.println("--------------------------------------------------------------------");
    }

}
