package com.voterapi.candidate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.voterapi.candidate.domain.Election;
import com.voterapi.candidate.repository.ElectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class ElectionService {

    private ElectionRepository electionRepository;
    private Environment environment;

    @Autowired
    public ElectionService(ElectionRepository electionRepository, Environment environment) {
        this.electionRepository = electionRepository;
        this.environment = environment;
        getAzureServiceBusElectionQueueMessages();
    }

    public void getAzureServiceBusElectionQueueMessages() {
        String connectionString = environment.getProperty("azure.service-bus.connection-string");
        String queueName = "elections.queue";

        try {
            IQueueClient queueReceiveClient = new QueueClient(
                    new ConnectionStringBuilder(connectionString, queueName), ReceiveMode.PEEKLOCK);

            queueReceiveClient.registerMessageHandler(new MessageHandler(queueReceiveClient, electionRepository),
                    new MessageHandlerOptions(1, false, Duration.ofMinutes(1)));
        } catch (InterruptedException | ServiceBusException e) {
            e.printStackTrace();
        }
    }

    static class MessageHandler implements IMessageHandler {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private ElectionRepository electionRepository;

        private IQueueClient client;

        public MessageHandler(IQueueClient client, ElectionRepository electionRepository) {
            this.client = client;
            this.electionRepository = electionRepository;
        }

        @Override
        public CompletableFuture<Void> onMessageAsync(IMessage iMessage) {
            System.out.format("Received message with sq#: %d and lock token: %s.",
                    iMessage.getSequenceNumber(), iMessage.getLockToken());
            return this.client.completeAsync(iMessage.getLockToken()).thenRunAsync(() ->
                    createElectionFromMessage(new String(iMessage.getBody()))
            );
        }

        @Override
        public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
            System.out.format(exceptionPhase + "-" + throwable.getMessage());
        }

        private void createElectionFromMessage(String electionMessage) {
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
}
