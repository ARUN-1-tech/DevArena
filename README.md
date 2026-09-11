# DevArena

> **"Code. Compete. Level Up."**  
> A gamified competitive programming platform where developers solve coding challenges, duel in real-time 1v1 battles, earn XP, unlock achievements, develop skills, and climb competitive leaderboards.

---

## 🚀 Module 01: Engineering Foundation

This repository contains **Module 01** of DevArena. The goal of this module is to establish the production-grade engineering foundation, modular package boundaries, infrastructure, and extension points for future feature modules.

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: React 18 with TypeScript
- **Bundler / Dev Server**: Vite
- **Styling**: Tailwind CSS (light, polished, gaming-inspired aesthetic)
- **Animations**: Framer Motion
- **Icons**: Lucide React
- **Routing**: React Router v6

### Backend
- **Framework**: Spring Boot 3 (Java 21+)
- **Security**: Spring Security 6 (stateless JWT architecture ready)
- **Data & Persistence**: Spring Data JPA, Hibernate, PostgreSQL
- **Migrations**: Flyway
- **Real-Time & Caching**: Spring WebSocket (STOMP), Spring Data Redis
- **Testing**: JUnit 5, Mockito, Spring Boot Test

### Infrastructure & DevOps
- **Containerization**: Docker, Docker Compose
- **Configuration**: Dynamic `.env` resolution via environment variables

---

## 📁 Repository Structure

```
dev-arena/
├── frontend/                      # React + TypeScript + Vite application
│   └── src/
│       ├── components/            # Reusable UI primitives, animations, layouts
│       │   ├── ui/                # Button, Card, Badge, Input
│       │   ├── animation/         # Motion wrappers and accents
│       │   ├── player/            # Player profile & rank widgets
│       │   ├── challenge/         # Challenge cards & difficulty tags
│       │   └── layout/            # Navbar, Footer
│       ├── pages/                 # HomePage, NotFoundPage
│       ├── layouts/               # Root layout shells
│       ├── routes/                # Centralized router
│       ├── hooks/                 # Custom React hooks (useSystemHealth)
│       ├── services/              # API clients
│       ├── types/                 # TypeScript type definitions
│       ├── lib/                   # Utility helpers
│       ├── data/                  # Static constants
│       └── styles/                # Tailwind global styles
│
├── backend/                       # Spring Boot 3 Modular Backend
│   └── src/main/java/com/devarena/
│       ├── config/                # CORS, Redis, WebSocket configurations
│       ├── security/              # Security filter chain, JWT provider, roles
│       ├── common/                # Unified ApiResponse, GlobalExceptionHandler, BaseAuditEntity
│       ├── user/                  # User management boundary
│       ├── challenge/             # Coding challenge boundary
│       ├── battle/                # 1v1 real-time battle boundary
│       ├── matchmaking/           # Matchmaking queue boundary
│       ├── ranking/               # ELO and leaderboard boundary
│       ├── achievement/           # Gamification badges boundary
│       ├── notification/          # Real-time notifications boundary
│       └── analytics/             # Player performance analytics boundary
│
├── docker/                        # Dockerfiles and container configurations
├── docs/                          # Architectural and technical documentation
│   ├── architecture.md            # System architecture blueprint
│   ├── api-conventions.md         # REST API standards and error envelope
│   └── development.md             # Developer onboarding and setup guide
│
├── docker-compose.yml             # Local multi-container development environment
├── .env.example                   # Environment variable template
└── README.md                      # Project documentation
```

---

## ⚡ Quick Start

### 1. Prerequisites
- Java 21+
- Node.js 18+
- Docker & Docker Compose (optional for local database & cache)

### 2. Environment Configuration
```bash
cp .env.example .env
```

### 3. Spin up Infrastructure (PostgreSQL & Redis)
```bash
docker-compose up -d postgres redis
```

### 4. Run Backend
```bash
cd backend
# Windows:
.\mvnw.cmd spring-boot:run
# Linux/macOS:
./mvnw spring-boot:run
```
Backend API will be accessible at: `http://localhost:8080/api/v1/health`

### 5. Run Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend development server will be accessible at: `http://localhost:5173`

---

## 🧪 Testing

### Backend Tests
```bash
cd backend
.\mvnw.cmd test
```

### Frontend Typecheck & Build
```bash
cd frontend
npm run build
```

---

## 🗺️ Future Module Roadmap

- **Module 02**: Database Entities, Schema Expansion & Repository Layer
- **Module 03**: Authentication & Authorization (JWT, Refresh Tokens, OAuth)
- **Module 04**: Coding Challenge Engine & Problem Catalog
- **Module 05**: Sandboxed Code Execution Service (Multi-language runner)
- **Module 06**: Real-Time 1v1 Battle System (WebSockets & State Machine)
- **Module 07**: Matchmaking & MMR Rating Engine
- **Module 08**: Gamification (XP, Achievements, Skill Progress, Leaderboards)
- **Module 09**: Real-Time Notifications & Social Mechanics
- **Module 10**: Analytics, Radar Skill Charts & Performance Insights
