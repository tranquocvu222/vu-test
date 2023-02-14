FROM gradle:7.3.3-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src/

WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11.0.13-jre-slim

EXPOSE 8083

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/rmt-auction-0.0.1-SNAPSHOT.jar /app/java.jar

ENTRYPOINT ["java","-jar","/app/java.jar"]