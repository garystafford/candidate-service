package com.voterapi.candidate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voterapi.candidate.domain.Election;
import com.voterapi.candidate.repository.ElectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElectionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ElectionRepository electionRepository;

    @Autowired
    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    /**
     * Consumes a new election message, deserializes, and save to MongoDB
     *
     * @param electionMessage
     */
    @RabbitListener(queues = "#{electionQueue.name}")
    public void getElectionMessage(String electionMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        TypeReference<Election> mapType = new TypeReference<Election>() {
        };

        Election election = null;

        try {
            election = objectMapper.readValue(electionMessage, mapType);
        } catch (IOException e) {
            logger.info(String.valueOf(e));
        }

        electionRepository.save(election);
        logger.debug("Election {} saved to MongoDB", election.toString());
    }


}
