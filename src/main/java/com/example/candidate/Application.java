package com.example.candidate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    final static String queueName = "candidates";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
