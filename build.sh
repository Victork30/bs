docker run -it --rm --name bs -v "$PWD"/microservice:/usr/src/app -w /usr/src/app maven:3.8.7-openjdk-18 mvn clean package
