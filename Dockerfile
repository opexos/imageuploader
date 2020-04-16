FROM openjdk:8-jdk-alpine
EXPOSE 60000
ADD . /code
WORKDIR /code
ENTRYPOINT ["./gradlew","bootRun"]