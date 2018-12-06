FROM openjdk:8-jdk-alpine
EXPOSE 8080
ADD . /code
WORKDIR /code
ENTRYPOINT ["./gradlew","bootRun"]