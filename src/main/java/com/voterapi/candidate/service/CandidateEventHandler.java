package com.voterapi.candidate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.IQueueClient;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.voterapi.candidate.domain.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CandidateEventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Environment environment;

    @Autowired
    public CandidateEventHandler(Environment environment) {
        this.environment = environment;
    }

    @HandleAfterCreate
    public void handleCandidateSave(Candidate candidate) {
        sendMessageAzureServiceBus(candidate);
    }

    private void sendMessageAzureServiceBus(Candidate candidate) {
        String connectionString = environment.getProperty("azure.service-bus.connection-string");
        String queueName = "candidates.queue";

        try {
            IQueueClient queueSendClient = new QueueClient(
                    new ConnectionStringBuilder(connectionString, queueName), ReceiveMode.PEEKLOCK);
            String message = serializeToJson(candidate);
            queueSendClient.sendAsync(new Message(message))
                    .thenRunAsync(queueSendClient::closeAsync);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ServiceBusException e) {
            e.printStackTrace();
        }
    }

    private String serializeToJson(Candidate candidate) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        try {
            jsonInString = mapper.writeValueAsString(candidate);
        } catch (JsonProcessingException e) {
            logger.info(String.valueOf(e));
        }

        logger.debug("Serialized message payload: {}", jsonInString);

        return jsonInString;
    }
}
