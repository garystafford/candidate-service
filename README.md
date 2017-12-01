[![Build Status](https://travis-ci.org/garystafford/candidate-service.svg?branch=kub-aks)](https://travis-ci.org/garystafford/candidate-service) [![Dependencies](https://app.updateimpact.com/badge/817200262778327040/candidate-service.svg?config=compile)](https://app.updateimpact.com/latest/817200262778327040/candidate-service) [![Layers](https://images.microbadger.com/badges/image/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own image badge on microbadger.com") [![Version](https://images.microbadger.com/badges/version/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own version badge on microbadger.com")

# Candidate Service

## Introduction

The Candidate [Spring Boot](https://projects.spring.io/spring-boot/) Service is a RESTful Web Service, backed by Azure CosmosDB (MongoDB) and Azure Service Bus. The Candidate service exposes several HTTP API endpoints, listed below. API users can retrieve a list candidates, add a new candidate, and inspect technical information about the running service.

![Voter API Architecture](Message_Queue_Diagram_Final.png)

## Service Endpoints

The service uses a context path of `/candidate`. All endpoints must be are prefixed with this sub-path.

Purpose                                                                                                                  | Method  | Endpoint
------------------------------------------------------------------------------------------------------------------------ | :------ | :----------------------------------------------------
Submit New Candidate                                                                                                     | POST    | [/candidate/candidates](http://localhost:8097/candidate/candidates)
Candidates                                                                                                               | GET     | [/candidate/candidates](http://localhost:8097/candidate/candidates)
Candidate Summary by Election                                                                                            | GET     | [/candidate/candidates/search/findByElectionContains?election={election}&projection=candidateVoterView](http://localhost:8097/candidate/candidates/search/findByElectionContains?election={election}&projection=candidateVoterView)
Candidate Summary by Election                                                                                            | GET     | [/candidate/candidates/summary/{election}](http://localhost:8097/candidate/candidates/summary/{election})
Service Info                                                                                                             | GET     | [/candidate/info](http://localhost:8097/candidate/info)
Service Health                                                                                                           | GET     | [/candidate/health](http://localhost:8097/candidate/health)
Service Metrics                                                                                                          | GET     | [/candidate/metrics](http://localhost:8097/candidate/metrics)
Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints | GET     | `/candidate/mappings`, `/candidate/env`, `/candidate/configprops`, etc.
Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/candidate/candidates`                                    | Various | DELETE, PATCH, PUT, page sort, size, etc.

The [HAL Browser](https://github.com/mikekelly/hal-browser) API browser for the `hal+json` media type is installed alongside the service. It can be accessed at `http://localhost:8097/candidate/actuator/`.

## New Candidate

Adding a new candidate requires an HTTP `POST` request to the `/candidate/candidates` endpoint, as follows:

HTTPie

```text
http POST http://localhost:8097/candidate/candidates \
  firstName='Mary' \
  lastName='Smith' \
  politicalParty='Test Party' \
  election='2016 Presidential Election'
```

cURL

```text
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{ "firstName": "Mary", "lastName": "Smith", "politicalParty": "Test Party", "election": "2016 Presidential Election" }' \
  "http://localhost:8097/candidate/candidates"
```

wget

```text
wget --method POST \
  --header 'content-type: application/json' \
  --body-data '{ "firstName": "Mary", "lastName": "Smith", "politicalParty": "Test Party", "election": "2016 Presidential Election" }' \
  --no-verbose \
  --output-document - http://localhost:8097/candidate/candidates
```

## Sample Output

Using [HTTPie](https://httpie.org/) command line HTTP client.

`http http://localhost:8097/candidate/simulation`

```json
{
    "message": "Simulation data created!"
}
```

`http http://localhost:8097/candidate/candidates/summary/2016%20Presidential%20Election`

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

`http http://localhost:8097/candidate/candidates`

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

`http POST http://localhost:8097/candidate/candidates \
    firstName='John' \
    lastName='Doe' \
    politicalParty='Test Party' \
    election='2016 Presidential Election'`

```json

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
    "politicalParty": "Test Party"
}
```
