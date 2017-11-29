package com.voterapi.candidate.service;

import com.voterapi.candidate.domain.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CandidateEventHandler {

    private CandidateService candidateService;

    @Autowired
    public CandidateEventHandler(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @HandleAfterCreate
    public void handleCandidateSave(Candidate candidate) {
        candidateService.sendMessageAzureServiceBus(candidate);
    }
}
