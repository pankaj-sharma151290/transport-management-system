FROM openjdk:8-jdk-alpine
MAINTAINER Pankaj Sharma
RUN mkdir log
COPY /target/TransportManagementSystem-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["sh","-c", "java -jar /app.jar"]