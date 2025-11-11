# === Stage 1: The Build ===
# We use the official Maven image (which has Java 21) as our "builder"
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml first. This is a cache optimization.
# Docker will only re-download dependencies if pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Run the build
RUN mvn clean package -DskipTests

# === Stage 2: The Final Image ===
# Start from a tiny, JRE-only image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# This is the magic: Copy *only* the built .jar file
# from the "builder" stage into this tiny final image
COPY --from=builder /app/target/*.jar /app.jar

# Tell the container what command to run on startup
ENTRYPOINT ["java", "-jar", "/app.jar"]