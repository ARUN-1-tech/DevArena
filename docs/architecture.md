# DevArena Architecture Documentation

> **DevArena**: *"Code. Compete. Level Up."*  
> Gamified competitive programming platform featuring real-time 1v1 battles, algorithmic challenges, skill trees, XP progression, achievements, and competitive ladder rankings.

---

## 1. Architectural Philosophy

DevArena is engineered as a **modular monolith** with clear package-level boundaries and strict decoupling between Frontend and Backend.

Key architectural tenets:
1. **Separation of Concerns**: Controllers only handle HTTP translation; business logic is encapsulated in isolated services; data access is confined to Spring Data repositories.
2. **Domain-Driven Boundary Organization**: Backend packages are organized around distinct business capabilities (`user`, `challenge`, `battle`, `matchmaking`, `ranking`, `achievement`, `notification`, `analytics`).
3. **Stateless REST + Stateful WebSockets**: HTTP APIs handle transactional and query operations via stateless REST endpoints, while real-time bidirectional interactions (matchmaking queues, countdown timers, live keystroke/test status, battle completions) run over STOMP WebSockets.
4. **Resilient Caching & In-Memory State**: Redis manages transient battle state, player presence, matchmaking queue storage, and high-frequency leaderboard rankings.
5. **Database Evolution**: PostgreSQL serves as the persistent source of truth, governed strictly through versioned Flyway migrations.

---

## 2. High-Level System Architecture

```
                                +---------------------------+
                                |      Browser Client       |
                                |  (React + Vite + Tailwind)|
                                +-------------+-------------+
                                              |
                             HTTP / REST      |      WebSocket (STOMP)
                                  v           |              v
                          +-------------------+--------------------+
                          |         DevArena Backend Engine        |
                          |            (Spring Boot 3)             |
                          +---------+-------------------+----------+
                                    |                   |
                        Spring Data | JPA        Jedis /| Lettuce
                                    v                   v
                          +-----------------+   +------------------+
                          |   PostgreSQL    |   |      Redis       |
                          | (Source of Truth|   | (Queue, State,   |
                          |   + Flyway)     |   |   Presence)      |
                          +-----------------+   +------------------+
```

---

## 3. Backend Modular Package Structure

```
backend/src/main/java/com/devarena/
├── DevArenaApplication.java       # Spring Boot main bootstrap
│
├── config/                        # Cross-cutting infrastructure configs
│   ├── CorsConfig.java            # WebMvc CORS policy
│   ├── RedisConfig.java           # RedisTemplate & serializers
│   └── WebSocketConfig.java       # STOMP broker & connection endpoints
│
├── security/                      # Spring Security 6 & JWT architecture
│   ├── SecurityConfig.java        # Security filter chain & URL rules
│   ├── JwtAuthenticationEntryPoint.java # 401 Unauthorized handling
│   ├── JwtTokenProvider.java      # Token generation/validation interface
│   └── UserRole.java              # Standard roles (ROLE_USER, ROLE_ADMIN)
│
├── common/                        # Shared cross-cutting components
│   ├── api/                       # Standard envelope (ApiResponse, ApiErrorResponse)
│   ├── exception/                 # GlobalExceptionHandler & base exceptions
│   ├── model/                     # BaseAuditEntity (created_at, updated_at)
│   └── controller/                # HealthController (/api/v1/health)
│
├── user/                          # User profile, credentials, preferences
├── challenge/                     # Coding challenges, test suites, categories
├── battle/                        # 1v1 battle rooms, live status, rounds
├── matchmaking/                   # Queue management, MMR matching algorithms
├── ranking/                       # ELO calculation, seasonal tiers, leaderboards
├── achievement/                   # Badges, unlock conditions, notifications
├── notification/                  # Real-time and persistent user notifications
└── analytics/                     # User skill radar, activity metrics
```

---

## 4. Frontend Architecture & Design Compatibility

The frontend is constructed using **Vite + React 18 + TypeScript + Tailwind CSS** with the following structural layout:

```
frontend/src/
├── components/
│   ├── ui/             # Core atomic design primitives (Button, Card, Badge, Input)
│   ├── animation/      # Framer motion transition helpers and glow elements
│   ├── player/         # Avatar, player badge, rating tags, XP meters
│   ├── challenge/      # Problem tags, difficulty pills, code preview stubs
│   └── layout/         # Navbar, Footer, Sidebar, Page Shell
│
├── pages/              # Routed view containers (HomePage, NotFoundPage)
├── layouts/            # Page layouts with shared navigation and theme wrappers
├── routes/             # Centralized routing definitions (react-router-dom)
├── hooks/              # Custom React hooks (useSystemHealth, useWebSocket)
├── services/           # Axios-based API client services
├── types/              # TypeScript interfaces (API envelopes, domain models)
├── lib/                # Utility helpers (cn class merger, axios instance)
├── data/               # Static configurations, navigation links, mock seeds
└── styles/             # Global CSS and Tailwind directives
```

### Visual Identity & Bolt.new Compatibility
- **Visual Identity**: DevArena — *"Code. Compete. Level Up."* A futuristic, gaming-inspired aesthetic built predominantly on a light, high-contrast palette with electric cyan, vivid violet, and emerald XP highlights.
- **Design Interoperability**: Reusable UI components do not rely on restrictive, opinionated component libraries. The design system uses Tailwind CSS with clean utility helpers, allowing designs generated in Bolt.new or Figma to be dropped in without refactoring.

---

## 5. Database Schema Blueprint (Planned Entities)

Module 01 introduces Flyway migrations (`V1__init_foundation.sql`) to manage the schema evolution. The target entity roadmap comprises:

1. **User Management**: `users`, `profiles`, `user_roles`, `friendships`
2. **Challenges**: `challenges`, `challenge_categories`, `skills`, `skill_progress`
3. **Competitive Arena**: `battles`, `battle_participants`, `matches`, `submissions`
4. **Gamification**: `ratings`, `leaderboards`, `achievements`, `user_achievements`, `xp_transactions`
5. **System & Engagement**: `notifications`, `system_metadata`

---

## 6. Real-time & Caching Foundation

- **WebSocket (STOMP)**:
  - Endpoint: `/ws` (with SockJS fallback enabled)
  - Application Destination Prefix: `/app`
  - Broadcast Topic Broker: `/topic`
  - User-Specific Queue Broker: `/queue`
- **Redis Strategy**:
  - `devarena:matchmaking:queue`: Active queue sorted set by MMR
  - `devarena:battle:{battleId}:state`: Live battle room state cache
  - `devarena:presence:{userId}`: Heartbeat and connection presence
  - `devarena:leaderboard:global`: Cached ELO standings for real-time reads
