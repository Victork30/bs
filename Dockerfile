FROM maven:3.8.7-openjdk-18
WORKDIR /app
COPY "$PWD"/microservice/target/resty-0.0.1-SNAPSHOT.jar /app/resty-0.0.1-SNAPSHOT.jar
COPY "$PWD"/microservice/src/config.properties /app/src/config.properties
CMD ["java", "-jar", "/app/resty-0.0.1-SNAPSHOT.jar"]
