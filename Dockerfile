FROM maven:3.6.1-jdk-8-slim AS build
RUN mkdir workspace && cd workspace
WORKDIR /workspace
COPY ./ /workspace/
RUN mvn -f pom.xml clean package

FROM openjdk:8-alpine
COPY --from=build /workspace/target/*.jar app.jar
RUN apk add ttf-dejavu
EXPOSE 8080
ENTRYPOINT ["java", "-jar","app.jar"]
