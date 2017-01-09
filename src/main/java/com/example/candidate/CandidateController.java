package com.example.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CandidateController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CandidateRepository candidateRepository;

    @RequestMapping(value = "/candidates", method = RequestMethod.GET)
    public ResponseEntity<List<Candidate>> getCandidates() {

        Query query = new Query();
        query.addCriteria(Criteria.where("candidate").exists(true));

        List<Candidate> results = mongoTemplate.findAll(Candidate.class);

        return ResponseEntity.status(HttpStatus.OK).body(results); // return 200 with payload
    }

    @RequestMapping(value = "/simulation", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> seedData() {

        candidateRepository.deleteAll();
        CandidateDemoList candidateDemoList = new CandidateDemoList();
        candidateRepository.save(candidateDemoList.getCandidates());
        Map<String, String> result = new HashMap<>();
        result.put("message", "random simulation data created");

        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }
}
