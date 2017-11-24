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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ElectionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ElectionRepository electionRepository;
    private IQueueClient queueClientReceiverElection;

    @Autowired
    public ElectionService(ElectionRepository electionRepository, IQueueClient queueClientReceiverElection) {
        this.electionRepository = electionRepository;
        this.queueClientReceiverElection = queueClientReceiverElection;
        getElectionMessageAzure();
    }

    public void getElectionMessageAzure() {

        try {
            queueClientReceiverElection.registerMessageHandler(new MessageHandler(queueClientReceiverElection),
                    new MessageHandlerOptions(1, false, Duration.ofMinutes(1)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ServiceBusException e) {
            e.printStackTrace();
        }
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

    static class MessageHandler implements IMessageHandler {
        private IQueueClient client;

        public MessageHandler(IQueueClient client) {
            this.client = client;
        }

        @Override
        public CompletableFuture<Void> onMessageAsync(IMessage iMessage) {
            System.out.format("Received message with sq#: %d and lock token: %s.",
                    iMessage.getSequenceNumber(), iMessage.getLockToken());
            return this.client.completeAsync(iMessage.getLockToken()).thenRunAsync(() ->
                    System.out.format("Completed message sq#: %d and locktoken: %s\n",
                            iMessage.getSequenceNumber(), iMessage.getLockToken()));
        }

        @Override
        public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
            System.out.format(exceptionPhase + "-" + throwable.getMessage());
        }

        private void waitForEnter(int seconds) {
            ExecutorService executor = Executors.newCachedThreadPool();
            try {
                executor.invokeAny(Arrays.asList(() -> {
                    System.in.read();
                    return 0;
                }, () -> {
                    Thread.sleep(seconds * 1000);
                    return 0;
                }));
            } catch (Exception e) {
                // absorb
            }
        }
    }
}
