FROM openjdk:21-jdk
MAINTAINER Samuel Abramov
COPY build/libs/gateway-0.0.1.jar gateway-0.0.1.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/gateway-0.0.1.jar"]

