package com.voterapi.candidate.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Candidate implements Serializable {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String politicalParty;
    private String election;
    private String homeState;
    private String politcalExperience;

    Candidate() {
        // unused constructor
    }

    public Candidate(String firstName,
                     String lastName,
                     String politicalParty,
                     String election,
                     String homeState,
                     String politcalExperience) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.politicalParty = politicalParty;
        this.election = election;
        this.homeState = homeState;
        this.politcalExperience = politcalExperience;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public String getElection() {
        return election;
    }

    public String getHomeState() {
        return homeState;
    }

    public String getPolitcalExperience() {
        return politcalExperience;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getFullName(), politicalParty);
    }
}
