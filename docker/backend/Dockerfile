# Multi-stage Dockerfile for DevArena Backend (Spring Boot 3.3.4 + Java 21)
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Copy Maven dependency descriptor first for caching layers
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Production JDK Runtime with sandbox compilers & runtimes
FROM eclipse-temurin:21-jdk-alpine AS runner
WORKDIR /app

# Install Python 3 and Node.js for multi-language execution sandbox
RUN apk add --no-cache python3 nodejs shadow

# Create unprivileged application user
RUN addgroup -S devarena && adduser -S devarena -G devarena
USER devarena:devarena

COPY --from=builder /workspace/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
