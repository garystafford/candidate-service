package com.voter_api.candidate.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

public class CandidateVoterView implements Serializable {

    private String fullName;
    private String politicalParty;
    private String election;

    CandidateVoterView() {
        // unused constructor
    }

    public CandidateVoterView(String fullName, String politicalParty, String election) {
        this.fullName = fullName;
        this.politicalParty = politicalParty;
        this.election = election;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public String getElection() {
        return election;
    }
}
