package com.example.candidate;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByElectionContains(@Param("election") String election);
}
