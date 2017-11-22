FROM openjdk:8u131-jdk-alpine
LABEL maintainer="Gary A. Stafford <garystafford@rochester.rr.com>"
ENV REFRESHED_AT 2017-11-21
VOLUME /tmp
EXPOSE 8080
RUN set -ex \
  && apk update \
  && apk upgrade \
  && apk add git
RUN mkdir /candidate \
  && git clone --depth 1 --branch build-artifacts \
      "https://github.com/garystafford/candidate-service.git" /candidate \
  && cd /candidate \
  && mv candidate-service-*.jar candidate-service.jar
CMD [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "candidate/candidate-service.jar" ]
