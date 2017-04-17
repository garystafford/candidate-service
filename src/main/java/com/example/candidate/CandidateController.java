package com.example.candidate;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CandidateController {

    private MongoTemplate mongoTemplate;

    private CandidateRepository candidateRepository;

    private List<String> results = new ArrayList<>();

    @Autowired
    public CandidateController(MongoTemplate mongoTemplate, CandidateRepository candidateRepository) {
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
    }

    @RequestMapping(value = "/candidates/summary", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<String>>> getCandidates() {

        Query query = new Query();
        query.addCriteria(Criteria.where("candidate").exists(true));

        List<Candidate> candidates = mongoTemplate.findAll(Candidate.class);
        candidates.sort(Comparator.comparing(Candidate::getLastName));
        results = new ArrayList<>();
        candidates.forEach(candidate -> results.add(candidate.toString()));
        return new ResponseEntity<>(Collections.singletonMap("candidates", results), HttpStatus.OK);
    }

    @RabbitListener(queues = "voter.rpc.requests")
    private List<String> getCandidatesMessageRpc(String requestMessage) {
        System.out.printf("Request message: %s%n", requestMessage);
        System.out.println("Sending RPC response message with list of candidates...");
        return results;
    }

    @RequestMapping(value = "/simulation", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulation() {

        candidateRepository.deleteAll();
        CandidateDemoList candidateDemoList = new CandidateDemoList();
        candidateRepository.save(candidateDemoList.getCandidates());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created!");

        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }
}
