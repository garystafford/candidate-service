package com.voterapi.candidate.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "candidateVoterView", types = { Candidate.class })
public interface CandidateVoterViewProjection {

    @Value("#{target.firstName} #{target.lastName}")
    String getFullName();

    String getPoliticalParty();

    String getElection();
}
