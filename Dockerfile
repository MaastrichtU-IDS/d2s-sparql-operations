FROM maven:3-jdk-8

LABEL maintainer "Vincent Emonet <vincent.emonet@maastrichtuniversity.nl>"

ENV APP_DIR /app
ENV TMP_DIR /tmp/dqa

WORKDIR $TMP_DIR

COPY . .

RUN mvn clean install && \
    mkdir $APP_DIR && \
    mv target/data-constructor-0.0.1-SNAPSHOT-jar-with-dependencies.jar $APP_DIR/data-constructor.jar && \
    rm -rf $TMP_DIR
    
WORKDIR $APP_DIR

ENTRYPOINT ["java","-jar","data-constructor.jar"]
