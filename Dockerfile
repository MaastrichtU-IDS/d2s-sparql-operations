FROM maven:3-jdk-11 as build

# Only runs if pom.xml changes. To avoid downloading dependencies everytime.
COPY pom.xml .
RUN mvn verify clean --fail-never

COPY src/ ./src/
RUN mvn package 


# Final running image
FROM openjdk:11-jre-slim

LABEL maintainer "Vincent Emonet <vincent.emonet@gmail.com>"

COPY --from=build target/d2s-sparql-operations-*-jar-with-dependencies.jar /app/d2s-sparql-operations.jar

WORKDIR /data
ENTRYPOINT ["java","-jar","/app/d2s-sparql-operations.jar"]
CMD ["-h"]