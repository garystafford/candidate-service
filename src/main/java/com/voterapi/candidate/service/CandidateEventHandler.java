package com.voterapi.candidate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voterapi.candidate.domain.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CandidateEventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RabbitTemplate rabbitTemplate;

    private Queue candidateQueue;

    private Candidate candidate;

    @Autowired
    public CandidateEventHandler(RabbitTemplate rabbitTemplate, Queue candidateQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.candidateQueue = candidateQueue;
    }


    @HandleAfterCreate
    public void handleCandidateSave(Candidate candidate) {
        this.candidate = candidate;
        sendMessage();
    }

    private void sendMessage() {
        rabbitTemplate.convertAndSend(
                candidateQueue.getName(), serializeToJson(candidate));

    }

    private String serializeToJson(Candidate candidate) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        try {
            jsonInString = mapper.writeValueAsString(candidate);
        } catch (JsonProcessingException e) {
            logger.info(String.valueOf(e));
        }

        logger.debug("Message payload: {}", jsonInString);

        return jsonInString;
    }

}
