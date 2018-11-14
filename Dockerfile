FROM maven:3-jdk-8

LABEL maintainer "Vincent Emonet <vincent.emonet@gmail.com>"

ENV APP_DIR /app
ENV TMP_DIR /tmp/dataformer

WORKDIR $TMP_DIR

COPY . .

RUN mvn clean install && \
    mkdir $APP_DIR && \
    mv target/sparql-dataformer-0.0.1-SNAPSHOT-jar-with-dependencies.jar $APP_DIR/sparql-dataformer.jar && \
    rm -rf $TMP_DIR
    
WORKDIR $APP_DIR

ENTRYPOINT ["java","-jar","sparql-dataformer.jar"]
