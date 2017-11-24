package com.voterapi.candidate;

import com.voterapi.candidate.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
    public class Application {

//    @Autowired
//    private static ElectionService electionService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
//        electionService.getElectionMessageAzure();
    }
}
