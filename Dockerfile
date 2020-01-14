FROM maven:3-jdk-8-alpine as build

# Only runs if pom.xml changes. To avoid downloading dependencies everytime.
COPY pom.xml .
RUN mvn verify clean --fail-never

COPY src/ ./src/
RUN mvn package 


# Final running image
FROM openjdk:8-jre-alpine

LABEL maintainer "Vincent Emonet <vincent.emonet@gmail.com>"

COPY --from=build target/d2s-sparql-operations-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/d2s-sparql-operations.jar

ENTRYPOINT ["java","-jar","/app/d2s-sparql-operations.jar"]
CMD ["-h"]