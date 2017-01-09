[![Build Status](https://travis-ci.org/garystafford/candidate-service.svg?branch=master)](https://travis-ci.org/garystafford/candidate-service) [![Dependencies](https://app.updateimpact.com/badge/817200262778327040/candidate-service.svg?config=compile)](https://app.updateimpact.com/latest/817200262778327040/candidate-service)

# com.example.candidate.Candidate Service

## Introduction

The Candidate [Spring Boot](https://projects.spring.io/spring-boot/) RESTful Web Service, backed by [MongoDB](https://www.mongodb.com/), is used for DevOps-related training and testing. The Candidate service exposes several HTTP API endpoints, listed below. API users can review a list candidates, submit a candidate, and inspect technical information about the running service. API users can also create random voting data by calling the `/simulation` endpoint.

## Quick Start for Local Development

The com.example.candidate.Candidate service requires MongoDB to be pre-installed and running locally, on port `27017`. To clone, build, test, and run the com.example.candidate.Candidate service, locally:

```bash
git clone https://github.com/garystafford/candidate-service.git
cd candidate-service
./gradlew clean cleanTest build
java -jar build/libs/candidate-service-0.1.0.jar
```

## Service Endpoints

By default, the service runs on `localhost`, port `8097`. By default, the service looks for MongoDB on `localhost`, port `27017`.

Purpose                                                                                                                  | Method  | Endpoint
------------------------------------------------------------------------------------------------------------------------ | :------ | :----------------------------------------------------
Create Random Sample Data                                                                                                | GET     | [/simulation](http://localhost:8097/simulation)
Submit New Candidate                                                                                                              | POST    | [/candidates](http://localhost:8097/candidates)
Service Info                                                                                                             | GET     | [/info](http://localhost:8097/info)
Service Health                                                                                                           | GET     | [/health](http://localhost:8097/health)
Service Metrics                                                                                                          | GET     | [/metrics](http://localhost:8097/metrics)
Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints | GET     | `/mappings`, `/env`, `/configprops`, etc.
Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/candidates`                                         | Various | DELETE, PATCH, PUT, page sort, size, etc.

The [HAL Browser](https://github.com/mikekelly/hal-browser) API browser for the `hal+json` media type is installed alongside the service. It can be accessed at `http://localhost:8097/actuator/`.

## New Candidate

Adding a new candidate, requires an HTTP `POST` request to the `/candidates` endpoint, as follows:

HTTPie

```text
http POST http://localhost:8097/candidates candidate="Jill Stein"
```

cURL

```text
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{ "candidate": "Jill Stein" }' \
  "http://localhost:8097/candidates"
```

wget

```text
wget --method POST \
  --header 'content-type: application/json' \
  --body-data '{ "candidate": "Jill Stein" }' \
  --no-verbose \
  --output-document - http://localhost:8097/candidates
```

## Sample Output

Using [HTTPie](https://httpie.org/) command line HTTP client.

`http http://localhost:8097/candidates`

```json
{
    "candidates": [
        "Chris Keniston",
        "Darrell Castle",
        "Donald Trump",
        "Gary Johnson",
        "Hillary Clinton",
        "Jill Stein"
    ]
}
```

`http http://localhost:8097/simulation`

```json
{
    "message": "random simulation data created"
}
```

`http POST http://localhost:8099/candidates candidate="Jill Stein"`

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8099/candidates/58279bda909a021142712fe7"
        },
        "candidate": {
            "href": "http://localhost:8099/candidates/58279bda909a021142712fe7"
        }
    },
    "candidate": "Jill Stein"
}
```

## Continuous Integration

The project's source code is continuously built and tested on every commit to [GitHub](https://github.com/garystafford/candidate-service), using [Travis CI](https://travis-ci.org/garystafford/candidate-service). If all unit tests pass, the resulting Spring Boot JAR is pushed to the `artifacts` branch of the [candidate-service-artifacts](https://github.com/garystafford/candidate-service-artifacts) GitHub repository. The JAR's filename is incremented with each successful build (i.e. `candidate-service-0.2.10.jar`).

![com.example.candidate.Vote Continuous Integration Pipeline](com.example.candidate.Candidate-CI.png)

## Spring Profiles

The com.example.candidate.Candidate service includes (3) Spring Boot Profiles, in a multi-profile YAML document: `src/main/resources/application.yml`. The profiles are `default`, `aws-production`, and `docker-production`. You will need to ensure your MongoDB instance is available at that `host` address and port of the profile you choose, or you may override the profile's properties.


```yaml
server:
  port: 8099
data:
  mongodb:
    host: localhost
    port: 27017
    database: candidates
logging:
  level:
    root: INFO
info:
  java:
    source: ${java.version}
    target: ${java.version}
---
spring:
  profiles: aws-production
data:
  mongodb:
    host: 10.0.1.6
logging:
  level:
    root: WARN
---
spring:
  profiles: docker-production
data:
  mongodb:
    host: mongodb
logging:
  level:
    root: WARN
```

All profile property values may be overridden on the command line, or in a .conf file. For example, to start the com.example.candidate.Candidate service with the `aws-production` profile, but override the `mongodb.host` value with a new host address, you might use the following command:

```bash
java -jar <name_of_the_jar_file> \
  --spring.profiles.active=aws-production \
  --spring.data.mongodb.host=<new_host_address>
  -Djava.security.egd=file:/dev/./urandom
```

# README

- [Spring Data MongoDB - Reference Documentation](http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Spring Boot Testing](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
- [Installing Spring Boot applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-install)
- [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
- [2016 Presidential Candidates](http://www.politics1.com/p2016.htm)
