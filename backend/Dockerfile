FROM eclipse-temurin:17-jre-alpine

# Create a non-root user for security and install docker CLI
RUN apk add --no-cache docker-cli && \
    addgroup -S spring && adduser -S spring -G spring -G docker

# Set working directory
WORKDIR /app

# Copy the built jar
COPY target/*.jar app.jar

# Change ownership to the non-root user
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring:spring

# Expose backend port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
