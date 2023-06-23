FROM openjdk:11-jre-slim-buster
ARG JAR_FILE=./target/authservice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /usr/app/authservice.jar
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","authservice.jar"]