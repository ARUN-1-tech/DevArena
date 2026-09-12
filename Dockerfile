# ==============================================================================
# DevArena Full-Stack Multi-Stage Production Dockerfile
# Provides a unified deployment container with Backend (Java 21), Nginx proxy,
# and Frontend React static SPA.
# ==============================================================================

# --- Stage 1: Build Frontend SPA ---
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci || npm install
COPY frontend/ ./
RUN npm run build

# --- Stage 2: Build Spring Boot Backend ---
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 3: Unified Production Runtime ---
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Install Python3, Node.js runtime for code execution sandboxing, and Supervisor/Nginx
RUN apk add --no-cache python3 nodejs shadow

# Create non-root application user
RUN addgroup -S devarena && adduser -S devarena -G devarena

# Copy backend JAR
COPY --from=backend-builder /app/backend/target/*.jar /app/app.jar

USER devarena:devarena

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
