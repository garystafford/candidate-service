package com.voterapi.candidate.configuration;

import com.microsoft.azure.servicebus.IQueueClient;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CandidateConfig {

    @Autowired
    private Environment environment;

    /**
     * Used for eventually consistent example
     *
     * @return
     */
    @Bean
    public Queue candidateQueue() {
        return new Queue("candidates.queue");
    }


    @Bean
    public Queue electionQueue() {
        return new Queue("elections.queue");
    }

    @Bean
    public IQueueClient queueClientReceiverElection() {
        String connectionString = getServiceBusConnectionString();
        String queueName = environment.getProperty("azure.service-bus.queue-name.election");
        try {
            return new QueueClient(new ConnectionStringBuilder(connectionString, queueName), ReceiveMode.PEEKLOCK);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ServiceBusException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getServiceBusConnectionString() {
        String connectionString = System.getenv("AZURE_SERVICE_BUS_CONNECTION_STRING");
        if (connectionString != null) return connectionString;
        return environment.getProperty("azure.service-bus.connection-string");
    }
}