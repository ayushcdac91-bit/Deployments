# Use an OpenJDK base image
FROM eclipse-temurin:21-jre

# Set working directory inside container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY target/auth-service.jar app.jar

# Expose the port your service runs on (default 8080 for Spring Boot)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java","-jar","app.jar"]
