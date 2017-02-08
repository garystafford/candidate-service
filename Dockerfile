FROM openjdk:8u111-jdk-alpine
LABEL maintainer "Gary A. Stafford <garystafford@rochester.rr.com>"
ENV REFRESHED_AT 2017-02-02
VOLUME /tmp
EXPOSE 8097
RUN set -ex \
  && apk update \
  && apk upgrade \
  && apk add git
RUN mkdir /candidate \
  && git clone --depth 1 --branch build-artifacts \
      "https://github.com/garystafford/candidate-service.git" /candidate \
  && cd /candidate \
  && mv candidate-service-*.jar candidate-service.jar
ENV JAVA_OPTS=""
CMD [ "java", "-Dspring.profiles.active=docker-development", "-Djava.security.egd=file:/dev/./urandom", "-jar", "candidate/candidate-service.jar"]
