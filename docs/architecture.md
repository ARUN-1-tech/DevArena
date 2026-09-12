# DevArena System Architecture & Design Blueprint

> **DevArena**: *"Code. Compete. Level Up."*  
> A high-performance, gamified competitive programming platform featuring real-time 1v1 battles, algorithmic challenges, code execution sandbox, skill tree progression, achievements, AI coaching, and platform governance.

---

## 1. High-Level System Architecture

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

## 2. Modular Monolith Architecture

The backend follows a domain-driven modular monolith pattern, isolating domain logic into discrete packages with explicit interfaces and zero cyclic dependencies:

### 2.1 Domain Boundaries
1. **`user`**: User registration, bcrypt password encryption, JWT authentication, refresh token rotation, player progression, and stats calculation.
2. **`challenge`**: Problem catalog, test suite metadata (public vs hidden tests), starter code stubs, constraints, and category tagging.
3. **`execution`**: Secure, process-isolated code execution pipeline. Sanitizes untrusted code, detects malicious system calls, manages process timeouts, captures standard output and standard error, and returns structured execution metrics.
4. **`submission`**: Persistent solution evaluation. Validates against hidden test cases, updates player solve counters, triggers XP awards, quest progression, and skill tree advancement.
5. **`matchmaking`**: High-concurrency matchmaking queue backed by Redis with in-memory fallback. Matches players based on MMR rating brackets.
6. **`battle`**: Synchronized real-time 1v1 battle room engine. Coordinates match countdowns, live opponent status synchronization, multi-language submissions, and ELO rating adjustments.
7. **`ranking`**: Dynamic ELO algorithm calculation, tier badge progression (Bronze, Silver, Gold, Platinum, Diamond, Master, Grandmaster), and global/friends leaderboards.
8. **`achievement`**: Platform badge catalog and rule-based unlock listeners (e.g., First Blood, Hot Streak, Polyglot, Veteran).
9. **`skill`**: Multi-tier skill hierarchy (Arrays, Strings, Algorithms, Dynamic Programming, Graphs) with progressive mastery tracking.
10. **`quest`**: Daily and weekly quest generator with real-time progress updates and XP rewards.
11. **`social`**: Player discovery, bidirectional friend requests, online presence tracking, team guild creation, and roster management.
12. **`notification`**: In-app persistent and real-time STOMP alert engine for battle invites, friend requests, and achievement unlocks.
13. **`ai`**: Socratic coding assistant providing hints, algorithmic guidance, and failure analysis without spoiling full solutions.
14. **`moderation` & `admin`**: Administrative Command HQ, player account suspension, report triage, anti-cheat code analysis, and audit logging.

---

## 3. Real-Time Communication Architecture

DevArena utilizes **Spring WebSocket with STOMP messaging**:
- **Connection Handshake**: Secured via JWT token passed either via `Authorization: Bearer <token>` or `token` query header in `WebSocketConfig`.
- **Channel Interceptor**: Extracts JWT, validates claims, loads `UserDetails`, and associates the authenticated principal with the WebSocket session.
- **Topics & Queues**:
  - `/topic/battle/{battleId}`: Broadcasts room state, player ready status, submission outcomes, and game completion.
  - `/user/queue/matchmaking`: Unicast notification when a match is found.
  - `/user/queue/notifications`: Unicast real-time notifications for social and achievement events.

---

## 4. Code Execution & Security Pipeline

Untrusted code execution runs through a defensive layered sandbox:
1. **Pattern Scanning**: Static blacklist analysis rejecting hazardous constructs (`Runtime.getRuntime()`, `ProcessBuilder`, `child_process`, `os.system`, file system write/delete calls, reflect APIs).
2. **Resource Isolation**: Processes execute in isolated temporary working directories with tight CPU and memory limits.
3. **Process Supervision**: Enforced hard execution timeout (default 5000ms) with process tree termination on timeout.
4. **Rate Limiting**: Redis-backed token bucket rate limiter (default 10 runs per minute per player) with in-memory fallback.

---

## 5. Production Containerization

The production environment is orchestrated via Docker Compose:
- **`postgres`**: PostgreSQL 16 Alpine with persistent volume and health check probe.
- **`redis`**: Redis 7 Alpine with appendonly persistence and ping health check.
- **`backend`**: Multi-stage Eclipse Temurin JRE 21 with non-root security context (`devarena:devarena`), optimized G1GC JVM parameters, and HikariCP connection pooling.
- **`frontend`**: Multi-stage Node 20 build producing minified static assets served by Nginx 1.27 Alpine with gzip compression, security headers, and reverse proxy routing for REST and WebSockets.
