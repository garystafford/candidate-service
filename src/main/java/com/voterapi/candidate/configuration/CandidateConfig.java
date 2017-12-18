package com.voterapi.candidate.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CandidateConfig {

    @Bean
    public Queue candidateQueue() {
        return new Queue("candidates.queue");
    }

    @Bean
    public Queue electionQueue() {
        return new Queue("elections.queue");
    }
}