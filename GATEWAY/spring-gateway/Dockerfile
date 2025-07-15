# === Build stage ===
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Set working directory inside the container
WORKDIR /app

# Copy project files (pom.xml + source code)
COPY pom.xml .
COPY src ./src

# Download dependencies and build the JAR (skip tests for speed)
RUN mvn clean package -DskipTests

# === Runtime stage ===
FROM eclipse-temurin:21-jdk-alpine

# Install curl
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose gateway default port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
