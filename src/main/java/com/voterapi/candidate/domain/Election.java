package com.voterapi.candidate.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Election implements Serializable {

    @Id
    private String id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    private ElectionType electionType;

    private String title;

    Election() {
        // unused constructor
    }

    public Election(Date date,
                    ElectionType electionType,
                    String title) {
        this.date = date;
        this.electionType = electionType;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public ElectionType getElectionType() {
        return electionType;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getTitle(), getDate());
    }
}


