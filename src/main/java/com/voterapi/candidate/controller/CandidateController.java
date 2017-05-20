package com.voterapi.candidate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voterapi.candidate.domain.Candidate;
import com.voterapi.candidate.domain.CandidateVoterView;
import com.voterapi.candidate.domain.Election;
import com.voterapi.candidate.repository.CandidateRepository;
import com.voterapi.candidate.repository.ElectionRepository;
import com.voterapi.candidate.service.CandidateDemoListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@RestController
public class CandidateController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private MongoTemplate mongoTemplate;
    private CandidateRepository candidateRepository;
    private ElectionRepository electionRepository;
    private CandidateDemoListService candidateDemoListService;

    @Autowired
    public CandidateController(MongoTemplate mongoTemplate,
                               CandidateRepository candidateRepository,
                               ElectionRepository electionRepository,
                               CandidateDemoListService candidateDemoListService) {
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
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
        logger.debug("Request message: {}", requestMessage);
        logger.debug("Sending RPC response message with list of candidates...");

        List<CandidateVoterView> candidates = getByElection(requestMessage);

        return serializeToJson(candidates);
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

        return groupResults.getMappedResults();
    }

    /**
     * Serialize list of candidates to JSON
     *
     * @param candidates
     * @return
     */
    private String serializeToJson(List<CandidateVoterView> candidates) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        final Map<String, List<CandidateVoterView>> dataMap = new HashMap<>();
        dataMap.put("candidates", candidates);

        try {
            jsonInString = mapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            logger.info(String.valueOf(e));
        }

        logger.debug("Serialized message payload: {}", jsonInString);

        return jsonInString;
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

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/candidates/drop", method = RequestMethod.GET)
    public ResponseEntity<Void> deleteAllCandidates() {
        candidateRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/elections/drop", method = RequestMethod.GET)
    public ResponseEntity<Void> deleteAllElections() {
        electionRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
