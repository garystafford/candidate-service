package com.voterapi.candidate.service;

import com.voterapi.candidate.domain.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CandidateEventHandler {

    private MessageBusUtilities messageBusUtilities;

    @Autowired
    public CandidateEventHandler(MessageBusUtilities messageBusUtilities) {
        this.messageBusUtilities = messageBusUtilities;
    }

    @HandleAfterCreate
    public void handleCandidateSave(Candidate candidate) {
        messageBusUtilities.sendMessageAzureServiceBus(candidate);
    }
}
