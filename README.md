[![Build Status](https://travis-ci.org/garystafford/candidate-service.svg?branch=kub-aks)](https://travis-ci.org/garystafford/candidate-service) [![Dependencies](https://app.updateimpact.com/badge/817200262778327040/candidate-service.svg?config=compile)](https://app.updateimpact.com/latest/817200262778327040/candidate-service) [![Layers](https://images.microbadger.com/badges/image/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own image badge on microbadger.com") [![Version](https://images.microbadger.com/badges/version/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own version badge on microbadger.com")

# Candidate Service

## Introduction

The Candidate [Spring Boot](https://projects.spring.io/spring-boot/) Service is a RESTful Web Service, backed by [MongoDB](https://www.mongodb.com/). The Candidate service exposes several HTTP API endpoints, listed below. API users can retrieve a list candidates, add a new candidate, and inspect technical information about the running service.

![Voter API Architecture](Message_Queue_Diagram_Final.png)

## Quick Start for Local Development

The Candidate service requires MongoDB to be running locally, on port `27017`, RabbitMQ running on `5672` and `15672`, and the Voter service to be running on `8099`. To clone, build, test, and run the Candidate service as a JAR file, locally:

```bash
git clone --depth 1 --branch rabbitmq \
  https://github.com/garystafford/candidate-service.git
cd candidate-service
./gradlew clean cleanTest build
java -jar build/libs/candidate-service-0.3.0.jar
```

## Getting Started with the API
The easiest way to get started with the Candidate and Voter services API, using [HTTPie](https://httpie.org/) from the command line:  
1. Create sample candidates: `http http://localhost:8097/candidate/simulation`  
2. View sample candidates: `http http://localhost:8097/candidate/candidates/summary/2016%20Presidential%20Election`  
3. Create sample voter data: `http http://localhost:8099/voter/simulation/2016%20Presidential%20Election`  
4. View sample voter results: `http http://localhost:8099/voter/results`

Alternately, for step 3 above, you can use service-to-service RPC IPC with RabbitMQ, to retrieve the candidates:  
`http http://localhost:8099/voter/simulation/rpc/2016%20Presidential%20Election`

Alternately, for step 3 above, you can use eventual consistency using RabbitMQ, to retrieve the candidates from MongoDB:  
`http http://localhost:8099/voter/simulation/db/2016%20Presidential%20Election`
## Service Endpoints

The service uses a context path of `/candidate`. All endpoints must be are prefixed with this sub-path.

Purpose                                                                                                                  | Method  | Endpoint
------------------------------------------------------------------------------------------------------------------------ | :------ | :----------------------------------------------------
Create Set of Sample Candidates                                                                                          | GET     | [/candidate/simulation](http://localhost:8097/candidate/simulation)
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

## Continuous Integration

The project's source code is continuously built and tested on every commit to [GitHub](https://github.com/garystafford/candidate-service), using [Travis CI](https://travis-ci.org/garystafford/candidate-service). If all unit tests pass, the resulting Spring Boot JAR is pushed to the `build-artifacts` branch of the [candidate-service](https://github.com/garystafford/candidate-service/tree/build-artifacts) GitHub repository. The JAR's filename is incremented with each successful build (i.e. `candidate-service-0.3.18.jar`).

![Vote Continuous Integration Pipeline](voter_flow_2.png)

## Spring Profiles

The Candidate service includes several Spring Boot Profiles, in a multi-profile YAML document: `src/main/resources/application.yml`. The profiles are `default`, `docker-development`, `docker-production`, and `aws-production`. You will need to ensure your MongoDB instance is available at that `host` address and port of the profile you choose, or you may override the profile's properties.

```yaml
azure:
  service-bus:
    connection-string: <sensitive_set_get_from_env_var>
endpoints:
  enabled: true
  sensitive: false
info:
  java:
    source: "${java.version}"
logging:
  level:
    root: INFO
management:
  health:
    mongo:
      enabled: true
  info:
    build:
      enabled: true
    git:
      mode: full
server:
  port: 8097
  context-path: /candidate
spring:
  application:
    name: Candidate Service
  data:
    mongodb:
      uri: <sensitive_set_get_from_env_var>
---
server:
  port: 8080
spring:
  profiles: kub-aks
```

All profile property values may be overridden on the command line, or in a .conf file. For example, to start the Candidate service with the `aws-production` profile, but override the `mongodb.host` value with a new host address, you might use the following command:

```bash
java -jar <name_of_jar_file> \
  --spring.profiles.active=aws-production \
  --spring.data.mongodb.host=<new_host_address>
  -Dlogging.level.root=DEBUG \
  -Djava.security.egd=file:/dev/./urandom
```

## References

- [Spring Data MongoDB - Reference Documentation](http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Spring Boot Testing](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
- [Installing Spring Boot applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-install)
- [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
- [2016 Presidential Candidates](http://www.politics1.com/p2016.htm)