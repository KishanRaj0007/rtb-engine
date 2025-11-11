# --- Stage 1: Build Stage ---
# Use a full Java 21 JDK image to build our application
FROM eclipse-temurin:21-jdk-jammy as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first
# This leverages Docker's layer caching. As long as pom.xml doesn't change,
# Docker won't re-download all the dependencies.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping the tests
RUN ./mvnw clean package -DskipTests


# --- Stage 2: Final Runtime Stage ---
# Use a slim Java 21 runtime-only image (JRE), which is smaller and more secure
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy ONLY the built JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port Spring Boot runs on (though this service doesn't use it, it's good practice)
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]