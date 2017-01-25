package com.example.candidate;

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

    @Autowired
    private CandidateController candidateController;

    @Before
    public void setup() {
        this.restTemplate.getForEntity("/simulation", String.class);
    }

    @Test
    public void getCandidatesReturnsListOfCandidates() throws Exception {
        String expectedCandidates = "{\"candidates\":[\"Darrell Castle (Constitution Party)\",\"Hillary Clinton " +
                "(Democratic Party)\",\"Gary Johnson (Libertarian Party)\",\"Chris Keniston " +
                "(Veterans Party)\",\"Jill Stein (Green Party)\",\"Donald Trump " +
                "(Republican Party)\"]}";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/candidates/summary", String.class);
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(responseEntity.getBody()).isEqualTo(expectedCandidates);

    }

    @Test
    public void postCandidateReturnsNewCandidate() throws Exception {
        Candidate candidate = new Candidate("John", "Doe", "Test Party");

        ResponseEntity<Candidate> responseEntity =
                this.restTemplate.postForEntity("/candidates", candidate, Candidate.class);
        assertThat(responseEntity.getStatusCode().value() == 201);
        assertThat(responseEntity.getBody().toString()).isEqualTo("John Doe (Test Party)");
    }

    @Test
    public void getSimulationReturnsExpectedMessage() throws Exception {
        String expectedResponse =
                "{\"message\":\"random simulation data created\"}";
        ResponseEntity<String> responseEntity =
                this.restTemplate.getForEntity("/simulation", String.class);
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }
}
