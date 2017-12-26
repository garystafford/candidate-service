[![Build Status](https://travis-ci.org/garystafford/candidate-service.svg?branch=gke)](https://travis-ci.org/garystafford/candidate-service) [![Dependencies](https://app.updateimpact.com/badge/817200262778327040/candidate-service.svg?config=compile)](https://app.updateimpact.com/latest/817200262778327040/candidate-service) [![Layers](https://images.microbadger.com/badges/image/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own image badge on microbadger.com") [![Version](https://images.microbadger.com/badges/version/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own version badge on microbadger.com")

# Candidate Service

## Introduction

The Candidate [Spring Boot](https://projects.spring.io/spring-boot/) Service is a RESTful Web Service, backed by MongoDB, using Atlas on GCP, and RabbitMQ, using CloudAMQP on GCP. It is part of the Voter API (see diagram below). The Candidate service exposes several HTTP API endpoints, listed below. API users can manage candidates and inspect technical information about the running service.

![Architecture](GKE_AMPQ_v1.png)

## Service Endpoints

The service uses a context path of `/candidate`. All endpoints must be are prefixed with this sub-path.

Purpose                                                                                                                  | Method  | Endpoint
------------------------------------------------------------------------------------------------------------------------ | :------ | :----------------------------------------------------
List All Service Endpoints                                                                                               | GET     | [/candidate/mappings](http://localhost:8097/candidate/mappings)
Create Candidate                                                                                                         | POST    | [/candidate/candidates](http://localhost:8097/candidate/candidates)
Read Candidate                                                                                                           | GET     | [/candidate/candidates/{id}](http://localhost:8097/candidate/candidates/{id})
Read Candidates                                                                                                          | GET     | [/candidate/candidates](http://localhost:8097/candidate/candidates)
Update Candidate                                                                                                         | PUT     | [/candidate/candidates/{id}](http://localhost:8097/candidate/candidates/{id})
Delete Candidate                                                                                                         | DELETE  | [/candidate/candidates/{id}](http://localhost:8097/candidate/candidates/{id})
Candidate Summary by Election                                                                                            | GET     | [/candidate/candidates/summary/{election}](http://localhost:8097/candidate/candidates/summary/{election})
Candidate Summary by Election                                                                                            | GET     | [/candidate/candidates/search/findByElectionContains?election={election}&projection=candidateVoterView](http://localhost:8097/candidate/candidates/search/findByElectionContains?election={election}&projection=candidateVoterView)
Drop All Elections                                                                                                       | POST    | [/candidate/drop/elections](http://localhost:8097/candidate/drop/elections)
Drop All Candidates                                                                                                      | POST    | [/candidate/drop/candidates](http://localhost:8097/candidate/drop/candidates)
Service Info                                                                                                             | GET     | [/candidate/info](http://localhost:8097/candidate/info)
Service Health                                                                                                           | GET     | [/candidate/health](http://localhost:8097/candidate/health)
Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints | GET     | `candidate/actuator`, `candidate/metrics`, `candidate/env`, `candidate/configprops`, etc.
Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/candidate/candidates`                                    | Various | page sort, size, etc.

The [HAL Browser](https://github.com/mikekelly/hal-browser) API browser for the `hal+json` media type is installed alongside the service. It can be accessed at `http://localhost:8097/candidate/actuator/`.

## New Candidate

Adding a new candidate requires an HTTP `POST` request to the `/candidate/candidates` endpoint, as follows:

HTTPie

```bash
http POST http://localhost:8097/candidate/candidates \
  firstName='Mary' \
  lastName='Smith' \
  politicalParty='Test Party' \
  election='2016 Presidential Election' \
  homeState='New York' \
  politcalExperience='Former 2-term Vice President of the United States'
```

cURL

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{ "firstName": "Mary", "lastName": "Smith", "politicalParty": "Test Party", "election": "2016 Presidential Election", "homeState": "New York", "politcalExperience": "Former 2-term Vice President of the United States" }' \
  "http://localhost:8097/candidate/candidates"
```

wget

```bash
wget --method POST \
  --header 'content-type: application/json' \
  --body-data '{ "firstName": "Mary", "lastName": "Smith", "politicalParty": "Test Party", "election": "2016 Presidential Election", "homeState": "New York", "politcalExperience": "Former 2-term Vice President of the United States" }' \
  --no-verbose \
  --output-document - http://localhost:8097/candidate/candidates
```

## Sample Output

Using [HTTPie](https://httpie.org/) command line HTTP client.

```bash
http http://localhost:8097/candidate/simulation
```

```json
{
    "message": "Simulation data created!"
}
```

```bash
http http://localhost:8097/candidate/candidates/summary/2016%20Presidential%20Election
```

```json
{
    "candidates": [
        {
            "election": "2016 Presidential Election",
            "fullName": "Darrell Castle",
            "politicalParty": "Constitution Party"
        },
        {
            "election": "2016 Presidential Election",
            "fullName": "Hillary Clinton",
            "politicalParty": "Democratic Party"
        },
        {
            "election": "2016 Presidential Election",
            "fullName": "Gary Johnson",
            "politicalParty": "Libertarian Party"
        }
    ]
}
```

```bash
http http://localhost:8097/candidate/candidates
```

```json
{
    "_embedded": {
        "candidates": [
            {
                "_links": {
                    "candidate": {
                        "href": "http://localhost:8097/candidate/candidates/590549471b8ebf721accc36b{?projection}",
                        "templated": true
                    },
                    "self": {
                        "href": "http://localhost:8097/candidate/candidates/590549471b8ebf721accc36b"
                    }
                },
                "election": "2012 Presidential Election",
                "firstName": "Rocky",
                "fullName": "Rocky Anderson",
                "lastName": "Anderson",
                "politicalParty": "Justice Party"
            },
            {
                "_links": {
                    "candidate": {
                        "href": "http://localhost:8097/candidate/candidates/590549471b8ebf721accc36c{?projection}",
                        "templated": true
                    },
                    "self": {
                        "href": "http://localhost:8097/candidate/candidates/590549471b8ebf721accc36c"
                    }
                },
                "election": "2016 Presidential Election",
                "firstName": "Darrell",
                "fullName": "Darrell Castle",
                "lastName": "Castle",
                "politicalParty": "Constitution Party"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://localhost:8097/candidate/profile/candidates"
        },
        "search": {
            "href": "http://localhost:8097/candidate/candidates/search"
        },
        "self": {
            "href": "http://localhost:8097/candidate/candidates"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 12,
        "totalPages": 1
    }
}
```

```bash
http POST http://localhost:8097/candidate/candidates \
  firstName='John' \
  lastName='Doe' \
  politicalParty='Test Party' \
  election='2016 Presidential Election' \
  homeState='New York' \
  politcalExperience='Former 2-term Vice President of the United States'
```

```json
{
    "_links": {
        "candidate": {
            "href": "http://localhost:8097/candidate/candidates/59054a341b8ebf721accc378{?projection}",
            "templated": true
        },
        "self": {
            "href": "http://localhost:8097/candidate/candidates/59054a341b8ebf721accc378"
        }
    },
    "election": "2016 Presidential Election",
    "firstName": "John",
    "fullName": "John Doe",
    "lastName": "Doe",
    "politicalParty": "Test Party",
    "homeState": "New York",
    "politcalExperience": "Former 2-term Vice President of the United States"
}
```
