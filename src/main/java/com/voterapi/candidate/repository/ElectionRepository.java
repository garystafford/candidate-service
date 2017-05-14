package com.voterapi.candidate.repository;

import com.voterapi.candidate.domain.Election;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ElectionRepository extends MongoRepository<Election, String> {
    List<ElectionRepository> findByElectionTypeContaining(@Param("electionType") String electionType);
}
