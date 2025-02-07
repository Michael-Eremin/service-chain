# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
#COPY ./src/main/proto/process.proto ./src/main/proto/process.proto
RUN mvn clean package -DskipTests
RUN mvn compile

# Stage 2: Create a lightweight runtime image
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/ai-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the gRPC port (default is 9090)
EXPOSE 6565

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]