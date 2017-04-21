[![Build Status](https://travis-ci.org/garystafford/candidate-service.svg?branch=rabbitmq)](https://travis-ci.org/garystafford/candidate-service) [![Dependencies](https://app.updateimpact.com/badge/817200262778327040/candidate-service.svg?config=compile)](https://app.updateimpact.com/latest/817200262778327040/candidate-service) [![Layers](https://images.microbadger.com/badges/image/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own image badge on microbadger.com") [![Version](https://images.microbadger.com/badges/version/garystafford/candidate-service.svg)](https://microbadger.com/images/garystafford/candidate-service "Get your own version badge on microbadger.com")

# Candidate Service

## Introduction

The Candidate [Spring Boot](https://projects.spring.io/spring-boot/) Service is a RESTful Web Service, backed by [MongoDB](https://www.mongodb.com/). The Candidate service exposes several HTTP API endpoints, listed below. API users can retrieve a list candidates, add a new candidate, and inspect technical information about the running service. API users can also create a sample list of candidates, based on the 2016 US Presidential Election, by calling the `/simulation` endpoint.

The Voter service is designed to work along with the [Candidate Service](https://github.com/garystafford/candidate-service), as part of a complete API. The Voter service is dependent on the Candidate service to supply a list of candidates. The Candidate service is called by the Voter service, using one of two methods:
1. [HTTP-based Synchronous IPC](https://www.nginx.com/blog/building-microservices-inter-process-communication/), when either the Voter service's `/candidates?election={election}` or `/simulation?election={election}` endpoints are called.
2. [Messaging-based Remote Procedure Call (RPC) IPC](http://www.rabbitmq.com/tutorials/tutorial-six-java.html), when either the Voter service's `/candidates/rpc?election={election}` or `/simulation/rpc?election={election}` endpoints are called.


## Quick Start for Local Development

The Candidate service requires MongoDB to be running locally, on port `27017`. To clone, build, test, and run the Candidate service as a JAR file, locally:

```bash
git clone --depth 1 --branch rabbitmq \
  https://github.com/garystafford/candidate-service.git
cd candidate-service
./gradlew clean cleanTest build
java -jar build/libs/candidate-service-0.3.0.jar
```

## Getting Started with the API
The easiest way to get started with the Candidate and Voter services API, using [HTTPie](https://httpie.org/) from the command line:  
1. Create sample candidates: `http http://localhost:8097/simulation`  
2. Create sample voter data: `http http://localhost:8099/simulation?election="2016 Presidential Election"`  
3. View sample voter results: `http http://localhost:8099/results`

## Service Endpoints

By default, the service runs on `localhost`, port `8097`. By default, the service looks for MongoDB on `localhost`, port `27017`.

Purpose                                                                                                                  | Method  | Endpoint
------------------------------------------------------------------------------------------------------------------------ | :------ | :----------------------------------------------------
Create Set of Sample Candidates                                                                                          | GET     | [/simulation](http://localhost:8097/simulation)
Submit New Candidate                                                                                                     | POST    | [/candidates](http://localhost:8097/candidates)
Candidate List                                                                                                           | GET     | [/candidates/summary?election={election}](http://localhost:8097/candidates/summary?election=)
Service Info                                                                                                             | GET     | [/info](http://localhost:8097/info)
Service Health                                                                                                           | GET     | [/health](http://localhost:8097/health)
Service Metrics                                                                                                          | GET     | [/metrics](http://localhost:8097/metrics)
Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints | GET     | `/mappings`, `/env`, `/configprops`, etc.
Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/candidates`                                    | Various | DELETE, PATCH, PUT, page sort, size, etc.

The [HAL Browser](https://github.com/mikekelly/hal-browser) API browser for the `hal+json` media type is installed alongside the service. It can be accessed at `http://localhost:8097/actuator/`.

## New Candidate

Adding a new candidate requires an HTTP `POST` request to the `/candidates` endpoint, as follows:

HTTPie

```text
http POST http://localhost:8097/candidates /
  firstName='Mary' /
  lastName='Smith' /
  politicalParty='Test Party' /
  election='2016 Presidential Election'
```

cURL

```text
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{ "firstName": "Mary", "lastName": "Smith", "politicalParty": "Test Party", "election": "2016 Presidential Election" }' \
  "http://localhost:8097/candidates"
```

wget

```text
wget --method POST \
  --header 'content-type: application/json' \
  --body-data '{ "firstName": "Mary", "lastName": "Smith", "politicalParty": "Test Party", "election": "2016 Presidential Election" }' \
  --no-verbose \
  --output-document - http://localhost:8097/candidates
```

## Sample Output

Using [HTTPie](https://httpie.org/) command line HTTP client.

`http http://localhost:8097/simulation`

```json
{
    "message": "Simulation data created!"
}
```

`http http://localhost:8097/candidates/summary`

```json
{
    "candidates": [
        "Darrell Castle (Constitution Party)",
        "Hillary Clinton (Democratic Party)",
        "Gary Johnson (Libertarian Party)",
        "Chris Keniston (Veterans Party)",
        "Jill Stein (Green Party)",
        "Donald Trump (Republican Party)"
    ]
}
```

`http http://localhost:8097/candidates`

```json
{
    "_embedded": {
        "candidates": [
            {
                "_links": {
                    "candidate": {
                        "href": "http://localhost:8097/candidates/5872517ea6e0de568921e77a"
                    },
                    "self": {
                        "href": "http://localhost:8097/candidates/5872517ea6e0de568921e77a"
                    }
                },
                "firstName": "Donald",
                "fullName": "Donald Trump",
                "lastName": "Trump",
                "politicalParty": "Republican Party",
                "election": "2016 Presidential Election"
            },
            {
                "_links": {
                    "candidate": {
                        "href": "http://localhost:8097/candidates/5872517ea6e0de568921e77f"
                    },
                    "self": {
                        "href": "http://localhost:8097/candidates/5872517ea6e0de568921e77f"
                    }
                },
                "firstName": "Hillary",
                "fullName": "Hillary Clinton",
                "lastName": "Clinton",
                "politicalParty": "Democratic Party",
                "election": "2016 Presidential Election"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://localhost:8097/profile/candidates"
        },
        "self": {
            "href": "http://localhost:8097/candidates"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 2,
        "totalPages": 1
    }
}
```

`http POST http://localhost:8097/candidates firstName='John' lastName='Doe' politicalParty='Test Party' election='2016 Presidential Election'`

```json
{
    "_links": {
        "candidate": {
            "href": "http://localhost:8097/candidates/5872de29a6e0de6ff01bd452"
        },
        "self": {
            "href": "http://localhost:8097/candidates/5872de29a6e0de6ff01bd452"
        }
    },
    "firstName": "John",
    "fullName": "John Doe",
    "lastName": "Doe",
    "politicalParty": "Test Party",
    "election": "2016 Presidential Election"
}
```

## Continuous Integration

The project's source code is continuously built and tested on every commit to [GitHub](https://github.com/garystafford/candidate-service), using [Travis CI](https://travis-ci.org/garystafford/candidate-service). If all unit tests pass, the resulting Spring Boot JAR is pushed to the `build-artifacts` branch of the [candidate-service](https://github.com/garystafford/candidate-service/tree/build-artifacts) GitHub repository. The JAR's filename is incremented with each successful build (i.e. `candidate-service-0.3.18.jar`).

![Vote Continuous Integration Pipeline](Voter-CI.png)

## Spring Profiles

The Candidate service includes several Spring Boot Profiles, in a multi-profile YAML document: `src/main/resources/application.yml`. The profiles are `default`, `docker-development`, `docker-production`, and `aws-production`. You will need to ensure your MongoDB instance is available at that `host` address and port of the profile you choose, or you may override the profile's properties.

```yaml
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
  info:
    build:
      enabled: true
    git:
      mode: full
server:
  port: 8097
spring:
  data:
    mongodb:
      database: candidates
      host: localhost
      port: 27017
  rabbitmq:
    host: localhost
---
spring:
  data:
    mongodb:
      host: mongodb
  rabbitmq:
    host: rabbitmq
  profiles: docker-local
---
---
endpoints:
  enabled: false
  health:
    enabled: true
  info:
    enabled: true
  sensitive: true
logging:
  level:
    root: WARN
management:
  info:
    build:
      enabled: false
    git:
      enabled: false
spring:
  data:
    mongodb:
      host: "10.0.1.6"
  rabbitmq:
    host: "10.0.1.8"
  profiles: aws-production
---
endpoints:
  enabled: false
  health:
    enabled: true
  info:
    enabled: true
  sensitive: true
logging:
  level:
    root: WARN
management:
  info:
    build:
      enabled: false
    git:
      enabled: false
spring:
  data:
    mongodb:
      host: mongodb
  rabbitmq:
    host: rabbitmq
  profiles: docker-production
```

All profile property values may be overridden on the command line, or in a .conf file. For example, to start the Candidate service with the `aws-production` profile, but override the `mongodb.host` value with a new host address, you might use the following command:

```bash
java -jar <name_of_jar_file> \
  --spring.profiles.active=aws-production \
  --spring.data.mongodb.host=<new_host_address>
  -Djava.security.egd=file:/dev/./urandom
```

## References

- [Spring Data MongoDB - Reference Documentation](http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Spring Boot Testing](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
- [Installing Spring Boot applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-install)
- [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
- [2016 Presidential Candidates](http://www.politics1.com/p2016.htm)
