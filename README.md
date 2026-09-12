# DevArena ⚔️

> **"Code. Compete. Level Up."**  
> A high-performance, full-stack gamified competitive programming platform where developers solve algorithmic challenges, duel in real-time 1v1 battles, develop mastery across an interactive skill tree, earn XP, and climb competitive global leaderboards.

---

[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![React 18](https://img.shields.io/badge/React-18.3-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.6-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)

---

## 🌟 Key Platform Features

- **🎮 Arena HQ & Progression**: Dynamic player dashboard featuring real-time MMR tier badges, XP progression bars, level milestones, daily quests, and active match history.
- **⚡ Real-Time 1v1 Battle Arena**: Synchronized multiplayer coding matches powered by STOMP over WebSockets. Real-time opponent status, countdown timers, live code evaluation, and ELO calculations.
- **💻 Interactive Code Lab**: Monaco Editor environment supporting Java, Python, and JavaScript. Includes custom test case runner, syntax highlighting, console output logs, and submission history.
- **🛡️ Multi-Language Execution Sandbox**: Secure, process-isolated local sandbox with static pattern scanning, memory and CPU throttling, timeout enforcement, and rate limiting.
- **🏆 Global Leaderboards & MMR Ladder**: Competitive ranking system with real-time updates, tier thresholds (Bronze through Grandmaster), win streak bonuses, and friends-only ladder filtering.
- **🌳 Interactive Skill Tree & Mastery**: RPG-style multi-tier skill progression across Arrays, Strings, Algorithms, Dynamic Programming, and Graphs with visual radar charts.
- **👥 Social Arena & Team Guilds**: Player search, bidirectional friend requests, online presence tracking, team guild creation, and team battle foundations.
- **🤖 Socratic AI Coding Coach**: Context-aware AI assistant providing hints, algorithmic explanations, complexity breakdowns, and failure analysis without spoiling solutions.
- **🛡️ Admin Command HQ & Governance**: Administrative portal for platform metrics, user moderation (account bans/unbans), report resolution, anti-cheat detection, and audit logging.
- **🐳 Production-Ready Containerization**: Fully dockerized with multi-stage non-root images, Nginx SPA routing, healthcheck probes, and single-command deployment.

---

## 🗺️ Complete Player Journey

```
LANDING PAGE
    │
    ▼
AUTHENTICATION ────────► REGISTER / LOGIN (JWT + Refresh Tokens)
    │
    ▼
ARENA HQ ──────────────► Level & XP Tracker, Daily Quests, Quick Match
    ├───────────────────► CHALLENGES CATALOG (Filter by Difficulty & Tags)
    │                         │
    │                         ▼
    │                     CODE LAB (Monaco Editor, Run Code, Test Suites)
    │                         │
    │                         ▼
    │                     SUBMISSION (Evaluation, XP Reward, Quest Progress, Skill Mastery)
    │
    ├───────────────────► 1v1 BATTLE ARENA
    │                         │
    │                         ▼
    │                     MATCHMAKING QUEUE (MMR-Based Match Finding)
    │                         │
    │                         ▼
    │                     SYNCHRONIZED BATTLE ROOM (Real-Time WebSocket, Live Status)
    │                         │
    │                         ▼
    │                     VICTORY / DEFEAT (Server-Authoritative MMR Adjustment)
    │
    ├───────────────────► LEADERBOARD & SKILL TREE (Global Rankings & Radar Analytics)
    │
    ├───────────────────► SOCIAL & TEAMS (Friend Requests, Team Guilds, Notifications)
    │
    ├───────────────────► AI COACH (Socratic Hints & Failure Diagnostics)
    │
    └───────────────────► ADMIN COMMAND HQ (Metrics, Moderation & Anti-Cheat)
```

---

## 🏗️ System Architecture

```
                                +---------------------------------------------+
                                |                Client Tier                  |
                                |     React 18 + Vite + Tailwind + Monaco     |
                                +----------------------+----------------------+
                                                       |
                             HTTP / REST (Port 8080)   |   WebSocket STOMP (Port 8080)
                                                       |
                                                       v
                                +---------------------------------------------+
                                |               Application Tier              |
                                |        Spring Boot 3 + Java 21 (JRE)        |
                                |                                             |
                                |  +---------------------------------------+  |
                                |  | Security Filter Chain (Stateless JWT) |  |
                                |  +-------------------+-------------------+  |
                                |                      |                      |
                                |  +-------------------+-------------------+  |
                                |  |          Domain Core Services         |  |
                                |  | - User & Auth      - Battle & Match   |  |
                                |  | - Challenge & Lab  - ELO & Ranking    |  |
                                |  | - Execution Sandbox- Social & Teams   |  |
                                |  | - Quest & Mastery  - AI & Governance  |  |
                                |  +-------------------+-------------------+  |
                                +----------------------+----------------------+
                                                       |
                                    +------------------+------------------+
                                    |                                     |
                                    v                                     v
                     +-----------------------------+       +-----------------------------+
                     |       PostgreSQL 16         |       |           Redis 7           |
                     |  - Flyway Migrations (V1-V8)|       |  - Matchmaking Queues       |
                     |  - Relational Schema        |       |  - Real-Time Presence       |
                     |  - Audit & Integrity Trails |       |  - Execution Rate Limiters  |
                     |  - HikariCP Connection Pool |       |  - Pub/Sub STOMP Bridge     |
                     +-----------------------------+       +-----------------------------+
```

---

## 🚀 Quick Start Guide

### Option A: Docker Compose (Single Command Full Stack)

1. Clone the repository:
   ```bash
   git clone https://github.com/ARUN-1-tech/DevArena.git
   cd DevArena
   ```

2. Copy the environment configuration:
   ```bash
   cp .env.example .env
   ```

3. Launch all services:
   ```bash
   docker compose up -d --build
   ```

4. Access the platform:
   - **Frontend Web App**: [http://localhost:3000](http://localhost:3000)
   - **Backend REST API**: [http://localhost:8080/api/v1](http://localhost:8080/api/v1)
   - **Actuator Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

### Option B: Local Development Setup

#### 1. Backend (Spring Boot 3 + Java 21)
```bash
cd backend
mvn spring-boot:run
```
*By default, local development uses an in-memory H2 database with automatic schema generation and mock/local sandbox execution. To use PostgreSQL and Redis, activate the `prod` profile or provide database environment variables.*

#### 2. Frontend (React 18 + Vite)
```bash
cd frontend
npm install
npm run dev
```
*Frontend dev server will be available at [http://localhost:5173](http://localhost:5173).*

---

## 🔑 Default Seed Credentials

For evaluation and testing, the platform automatically seeds an administrator account:

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Platform Administrator** | `admin@devarena.io` | `Password123` | Full Access + Admin Command HQ |
| **Player Account** | Self-registered | Configured at signup | Full Player Experience |

---

## 🧪 Testing & Quality Assurance

### Run Backend Unit & Integration Tests
```bash
cd backend
mvn clean test
```
*All 59+ test suites across auth, execution, battles, matchmaking, progression, ratings, skills, and moderation pass with 0 failures.*

### Run Frontend Typecheck & Build
```bash
cd frontend
npm run typecheck
npm run build
```
*TypeScript strict mode verification and minified production bundle generation.*

---

## 📂 Repository Layout

```
DevArena/
├── backend/                       # Spring Boot 3 Modular Monolith
│   ├── src/main/java/com/devarena/
│   │   ├── config/                # CORS, Redis, WebSocket STOMP
│   │   ├── security/              # JWT Token Provider, Security Filter Chain
│   │   ├── user/                  # Player Profile, Auth, Progression
│   │   ├── challenge/             # Problem Catalog, Test Cases
│   │   ├── execution/             # Process Sandbox, Security Scanners
│   │   ├── submission/            # Persistent Code Submissions
│   │   ├── matchmaking/           # Redis Queue, ELO Brackets
│   │   ├── battle/                # 1v1 Synchronized Battle Rooms
│   │   ├── ranking/               # ELO Rating Engine, Leaderboards
│   │   ├── achievement/           # Badges, Unlock Event Listeners
│   │   ├── skill/                 # Skill Hierarchy, Mastery Matrix
│   │   ├── quest/                 # Daily Quest System
│   │   ├── social/                # Friend Graph, Team Guilds
│   │   ├── notification/          # Persistent & Real-Time Alerts
│   │   ├── ai/                    # Socratic AI Coach Engine
│   │   └── admin/                 # Governance HQ, Moderation, Anti-Cheat
│   └── src/main/resources/
│       ├── db/migration/          # Flyway SQL Migrations (V1 to V8)
│       └── application-prod.yml   # Production Spring Configuration
│
├── frontend/                      # React 18 + Vite + TypeScript Application
│   ├── src/
│   │   ├── components/            # UI Primitives, Code Editor, Battle Widgets
│   │   ├── pages/                 # Arena HQ, Code Lab, Battles, Admin HQ
│   │   ├── layouts/               # Responsive Navigation & Admin Shell
│   │   ├── services/              # REST Clients & STOMP WebSocket Service
│   │   ├── contexts/              # Auth, WebSocket, Notification Contexts
│   │   └── types/                 # TypeScript Interfaces & API Envelopes
│
├── docker/                        # Container Configurations
│   ├── backend/Dockerfile         # Multi-stage Eclipse Temurin JRE 21
│   └── frontend/Dockerfile        # Multi-stage Node 20 -> Nginx 1.27 Alpine
│
├── docs/                          # Architectural & Technical Documentation
│   ├── architecture.md            # System Architecture & Design Blueprint
│   └── api-conventions.md         # REST & WebSocket API Specification
│
├── docker-compose.yml             # Orchestration for Full Stack
├── .env.example                   # Environment Configuration Template
└── README.md                      # Platform Overview & Documentation
```

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
