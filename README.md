# DevArena ⚔️

> **"Code. Compete. Level Up."**  
> A high-performance, full-stack gamified competitive programming and real-time battle arena platform. Developers solve 128+ algorithmic and core CS katas, duel in real-time 1v1 multiplayer matches, master interactive skill trees, earn XP, and climb competitive global leaderboards.

---

[![DevArena CI](https://github.com/ARUN-1-tech/DevArena/actions/workflows/ci.yml/badge.svg)](https://github.com/ARUN-1-tech/DevArena/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![React 18](https://img.shields.io/badge/React-18.3-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.6-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

## 🌟 Key Platform Features

- **📚 Complete Problem Archive (128+ Challenges)**: Unified, live database-driven problem library covering Arrays & Two Pointers, Strings, Linked Lists, Stack & Queue, Trees, Graphs, DP, Binary Search, Backtracking, Heaps, Bit Manipulation, Greedy, SQL, DBMS, Operating Systems, Computer Networks, Aptitude, Puzzles, and System Design.
- **⚡ Real-Time 1v1 Battle Arena**: Synchronized multiplayer coding matches powered by STOMP over WebSockets with matchmaking queues, live opponent status broadcast, and ELO calculations.
- **💻 Interactive Code Lab**: Monaco Editor environment supporting Java, Python, and JavaScript with 2-axis LeetCode-style resizers, syntax highlighting, test case runners, console logs, and submission history.
- **🧠 Interactive Non-Coding & MCQ Quiz Engine**: Radio-button MCQ evaluation with instant server-side scoring, XP awards, hints, and detailed explanations.
- **🛡️ Multi-Language Execution Sandbox**: Secure, process-isolated local sandbox with static pattern scanning, memory and CPU throttling, timeout enforcement, and rate limiting.
- **🏆 Global Leaderboards & MMR Ladder**: Competitive ranking system with real-time updates, tier thresholds (Bronze through Grandmaster), win streak bonuses, and friends-only ladder filtering.
- **🌳 Interactive Skill Tree & Mastery**: RPG-style multi-tier skill progression across 24 core topics with visual radar analytics.
- **👥 Social Arena & Team Guilds**: Player search, bidirectional friend requests, online presence tracking, team guild creation, and notifications.
- **🤖 Socratic AI Coding Coach**: Context-aware AI assistant providing hints, algorithmic explanations, complexity breakdowns, and failure analysis without spoiling solutions.
- **🛡️ Admin Command HQ & Governance**: Administrative portal for platform metrics, user moderation (account bans/unbans), report resolution, anti-cheat detection, audit logging, and bulk JSON/CSV dataset importing.

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
                     |  - Flyway Migrations (V1-V9)|       |  - Matchmaking Queues       |
                     |  - Relational Schema        |       |  - Real-Time Presence       |
                     |  - Audit & Integrity Trails |       |  - Execution Rate Limiters  |
                     |  - HikariCP Connection Pool |       |  - Pub/Sub STOMP Bridge     |
                     +-----------------------------+       +-----------------------------+
```

For complete technical specifications, see [`docs/architecture.md`](docs/architecture.md).

---

## 🚀 Quick Start (Local Development)

### 1. Prerequisites
- **Java 21 JDK** (Eclipse Temurin or OpenJDK)
- **Node.js 20+** & **npm**
- **Maven 3.9+** (or bundled IntelliJ Maven)
- **Docker & Docker Compose** (optional for local DB/Redis)

### 2. Clone Repository
```bash
git clone https://github.com/ARUN-1-tech/DevArena.git
cd DevArena
```

### 3. Run with In-Memory / Dev Mode (Zero external dependencies)
```bash
# Terminal 1: Backend
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

# Terminal 2: Frontend
cd frontend
npm install
npm run dev
```

The app will be available at:
- **Frontend**: [http://localhost:5173/](http://localhost:5173/)
- **Backend API**: [http://localhost:8080/](http://localhost:8080/)

---

## 🐳 Quick Start (Docker Compose)

Deploy the entire stack (PostgreSQL, Redis, Backend, and Frontend) in one command:

```bash
cp .env.example .env
docker compose up -d --build
```

Access the production frontend at [http://localhost:3000](http://localhost:3000) or [http://localhost:80](http://localhost:80).

---

## 🔐 Default Credentials

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@devarena.io` | `Password123` | Full Admin Command HQ, Moderation, Bulk Imports |
| **Test Player** | `player@devarena.io` | `Password123` | Standard Player, 1v1 Battles, Problem Solving |

---

## 📖 Documentation & Guides

- **[Deployment & Hosting Guide](docs/deployment.md)**: VPS Setup, SSL (Let's Encrypt), Render, Railway, AWS, and Kubernetes.
- **[System Architecture](docs/architecture.md)**: Modular Monolith, STOMP WebSockets, and Sandbox Isolation.
- **[API Conventions](docs/api-conventions.md)**: REST endpoints, JWT authentication headers, and WebSocket topics.
- **[Security Policy](SECURITY.md)**: Vulnerability disclosure and sandbox security details.

---

## 📄 License

DevArena is open-source software licensed under the [MIT License](LICENSE).
