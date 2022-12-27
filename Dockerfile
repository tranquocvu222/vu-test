FROM gradle:7.3.3-jdk11 AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle gradle.properties settings.gradle /home/gradle/app/
COPY gradle /home/gradle/app/gradle

WORKDIR /home/gradle/app
RUN gradle build -Pspeed=true --no-daemon -i --stacktrace

FROM gradle:7.3.3-jdk11 AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/app/
WORKDIR /usr/src/app
#Uncomment if need to check before build jar
#RUN gradle clean check
RUN gradle -Pdocker clean bootJar -i --stacktrace --build-cache

FROM openjdk:11.0.13-jre-slim
EXPOSE 8080
#TODO Run Docker as a non-root user
USER root
WORKDIR /usr/src/java-app
COPY --from=build /usr/src/app/build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
