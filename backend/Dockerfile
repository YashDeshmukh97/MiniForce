# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Create a non-root user for security and install docker CLI
RUN apk add --no-cache docker-cli && \
    addgroup -S docker && \
    addgroup -S spring && \
    adduser -S spring -G spring -G docker

# Set working directory
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to the non-root user
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring:spring

# Expose backend port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
