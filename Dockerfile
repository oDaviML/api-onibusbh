FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]