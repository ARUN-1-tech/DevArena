# DevArena Local Development Guide

This guide walks you through setting up and running DevArena locally for development.

---

## 1. Prerequisites

- **Java**: OpenJDK 21 or higher (Java 25 supported).
- **Node.js**: v18 or higher (Node v24 tested).
- **Package Manager**: npm, pnpm, or yarn.
- **Docker & Docker Compose**: Recommended for local PostgreSQL & Redis.
- **Git**: For version control.

---

## 2. Quickstart with Docker Compose

Run the infrastructure stack (PostgreSQL + Redis + Spring Boot backend) with one command:

```bash
# 1. Copy environment variables
cp .env.example .env

# 2. Launch PostgreSQL and Redis in the background
docker-compose up -d postgres redis

# 3. Verify services are healthy
docker-compose ps
```

---

## 3. Running the Backend Locally

If running outside Docker:

```bash
cd backend

# On Windows (PowerShell):
.\mvnw.cmd spring-boot:run

# On Linux/macOS:
./mvnw spring-boot:run
```

The backend server boots on `http://localhost:8080`.  
Verify health endpoint:
```bash
curl http://localhost:8080/api/v1/health
```

### Running Backend Tests
```bash
cd backend

# Execute all unit and context tests
.\mvnw.cmd test
```

---

## 4. Running the Frontend Locally

```bash
cd frontend

# 1. Install dependencies
npm install

# 2. Start Vite development server
npm run dev
```

The frontend will run at `http://localhost:5173`.  
Vite is preconfigured to proxy `/api` and `/ws` calls directly to `http://localhost:8080`.

### Typecheck and Production Build
```bash
cd frontend
npm run build
```

---

## 5. Database Migrations (Flyway)

Flyway migrations are located at:
```
backend/src/main/resources/db/migration/
```

- Naming convention: `V{version}__{description}.sql` (e.g. `V1__init_foundation.sql`, `V2__create_user_tables.sql`).
- Migrations run automatically when Spring Boot starts up.
- Never modify an already executed migration in shared environments.

---

## 6. Code Style & Quality Guidelines

- **SOLID Principles**: Single responsibility classes with dependency injection via constructor.
- **No Controller Logic**: Controllers must exclusively validate input and delegate to services.
- **Centralized Errors**: Throw domain-specific exceptions inheriting from `DevArenaException` rather than returning raw HTTP error codes in controllers.
- **Frontend Primitives**: Build lightweight, unopinionated UI primitives in `src/components/ui` that can easily accept styles from Bolt.new.
