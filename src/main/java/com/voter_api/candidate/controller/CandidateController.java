package com.voter_api.candidate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voter_api.candidate.domain.Candidate;
import com.voter_api.candidate.domain.CandidateVoterView;
import com.voter_api.candidate.repository.CandidateRepository;
import com.voter_api.candidate.service.CandidateDemoListService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@RestController
public class CandidateController {

    private MongoTemplate mongoTemplate;

    private CandidateRepository candidateRepository;

    private CandidateDemoListService candidateDemoListService;

    @Autowired
    public CandidateController(MongoTemplate mongoTemplate,
                               CandidateRepository candidateRepository,
                               CandidateDemoListService candidateDemoListService) {
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
        this.candidateDemoListService = candidateDemoListService;
    }

    /**
     * Returns a summary of all candidates, sorted by last name
     *
     * @return
     */
    @RequestMapping(value = "/candidates/summary", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CandidateVoterView>>> getCandidates() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("firstName", "lastName", "politicalParty", "election")
                        .andExpression("concat(firstName,' ', lastName)")
                        .as("fullName").andExclude("_id"),
                sort(Sort.Direction.ASC, "lastName")
        );

        AggregationResults<CandidateVoterView> groupResults
                = mongoTemplate.aggregate(aggregation, Candidate.class, CandidateVoterView.class);
        List<CandidateVoterView> candidates = groupResults.getMappedResults();

        return new ResponseEntity<>(Collections.singletonMap("candidates", candidates), HttpStatus.OK);
    }


    /**
     * Returns a summary of all candidates, by election, sorted by last name
     *
     * @param election
     * @return
     */
    @RequestMapping(value = "/candidates/summary/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CandidateVoterView>>> getCandidatesByElection(@PathVariable("election") String election) {

        List<CandidateVoterView> candidates = getByElection(election);
        return new ResponseEntity<>(Collections.singletonMap("candidates", candidates), HttpStatus.OK);
    }

    /**
     * Consumes message from queue containing election query
     * Produces candidate list based on election query
     *
     * @param requestMessage
     * @return
     */
    @RabbitListener(queues = "voter.rpc.requests")
    private String getCandidatesMessageRpc(String requestMessage) {

        System.out.printf("Request message: %s%n", requestMessage);
        System.out.println("Sending RPC response message with list of candidates...");

        List<CandidateVoterView> candidates = getByElection(requestMessage);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(candidates);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonInString;
    }

    /**
     * Common MongoDB query to find candidates by election
     *
     * @param election
     * @return
     */
    private List<CandidateVoterView> getByElection(String election) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("election").is(election)),
                project("firstName", "lastName", "politicalParty", "election")
                        .andExpression("concat(firstName,' ', lastName)")
                        .as("fullName"),
                sort(Sort.Direction.ASC, "lastName")
        );

        AggregationResults<CandidateVoterView> groupResults
                = mongoTemplate.aggregate(aggregation, Candidate.class, CandidateVoterView.class);
        List<CandidateVoterView> candidates = groupResults.getMappedResults();
        return candidates;
    }

    /**
     * Populates database with list of candidates
     *
     * @return
     */
    @RequestMapping(value = "/simulation", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulation() {

        candidateRepository.deleteAll();
        candidateRepository.save(candidateDemoListService.getCandidates());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created!");

        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }
}
