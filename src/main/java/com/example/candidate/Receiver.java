package com.example.candidate;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Receiver {

    public void receiveMessage(List<String> message) {
        try {
            System.out.println("Received <" + message.get(0) + ">");
            System.out.println("Received <" + message.get(1) + ">");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
