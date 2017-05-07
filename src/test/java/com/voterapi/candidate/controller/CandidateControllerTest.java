package com.voterapi.candidate.controller;

import com.voterapi.candidate.domain.Candidate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CandidateControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        restTemplate.getForEntity("/simulation", String.class);
    }

    @Test
    public void getCandidatesReturnsListOfCandidates() throws Exception {
       String election = "2016 Presidential Election";
        String expectedCandidates = "{\"candidates\":[{\"fullName\":\"Darrell Castle\"";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(
                String.format("/candidates/summary/election/%s", election), String.class);
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(responseEntity.getBody()).contains(expectedCandidates);

    }

    @Test
    public void postCandidateReturnsNewCandidate() throws Exception {
        Candidate candidate = new Candidate(
                "John",
                "Doe",
                "Test Party",
                "2017 Test Election"
        );

        ResponseEntity<Candidate> responseEntity =
                restTemplate.postForEntity("/candidates", candidate, Candidate.class);
        assertThat(responseEntity.getStatusCode().value() == 201);
        assertThat(responseEntity.getBody().toString()).isEqualTo("John Doe (Test Party)");
    }

    @Test
    public void getSimulationReturnsExpectedMessage() throws Exception {
        String expectedResponse =
                "{\"message\":\"Simulation data created!\"}";
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("/simulation", String.class);
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }
}
