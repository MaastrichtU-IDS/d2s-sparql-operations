FROM maven:3-jdk-8-alpine

LABEL maintainer "Vincent Emonet <vincent.emonet@gmail.com>"

ENV APP_DIR /app
ENV TMP_DIR /tmp/operations

WORKDIR $TMP_DIR

# Only runs if pom.xml changes. To avoid downloading dependencies everytime.
COPY pom.xml .
RUN mvn verify clean --fail-never

COPY src/ ./src/
RUN mvn package && \
    mkdir $APP_DIR && \
    mv target/data2services-sparql-operations-0.0.1-SNAPSHOT-jar-with-dependencies.jar $APP_DIR/data2services-sparql-operations.jar && \
    rm -rf $TMP_DIR
    
WORKDIR $APP_DIR

ENTRYPOINT ["java","-jar","data2services-sparql-operations.jar"]
CMD ["-h"]