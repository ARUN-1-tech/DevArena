# DevArena REST & Real-Time API Conventions

All HTTP REST endpoints and WebSocket STOMP topics in DevArena follow unified design contracts, predictable schemas, standard HTTP status codes, and deterministic error responses.

---

## 1. Base URL & Protocol Foundations

- **Base REST Path**: `/api/v1`
- **STOMP WebSocket Endpoints**:
  - `/ws` (with SockJS fallback)
  - `/ws-direct` (raw WebSocket)
- **Broker Prefixes**:
  - `/topic`: Public/room broadcasts (e.g., `/topic/battle/{battleId}`)
  - `/queue` and `/user/queue`: Point-to-point private messages (e.g., `/user/queue/notifications`, `/user/queue/matchmaking`)
  - `/app`: Client-to-server messaging destinations

---

## 2. Standard Response Envelopes

### 2.1 Success Envelope (`ApiResponse<T>`)
Every successful HTTP response is wrapped in a consistent payload:
```json
{
  "success": true,
  "message": "Resource retrieved successfully",
  "data": { ... },
  "timestamp": "2026-09-12T03:30:00Z"
}
```

### 2.2 Error Envelope (`ApiErrorResponse`)
Captured globally by `GlobalExceptionHandler`:
```json
{
  "timestamp": "2026-09-12T03:30:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Username is already taken",
  "path": "/api/v1/auth/register"
}
```

### 2.3 Key Error Codes
| Code | HTTP Status | Meaning |
| :--- | :--- | :--- |
| `VALIDATION_ERROR` | 400 | Payload bean validation failure (blank, regex mismatch) |
| `BAD_REQUEST` | 400 | Illegal arguments or state transitions |
| `UNAUTHORIZED` | 401 | Missing, expired, or invalid JWT Bearer token |
| `FORBIDDEN` | 403 | Insufficient role or access privileges |
| `RESOURCE_NOT_FOUND` | 404 | Target entity not found |
| `CONFLICT` | 409 | Duplicate unique constraint (e.g., username, email, team name) |
| `RATE_LIMITED` | 429 | Execution rate limit exceeded |
| `INTERNAL_SERVER_ERROR` | 500 | Unhandled platform error |

---

## 3. Core REST API Catalog

### 3.1 Authentication & Profile (`/api/v1/auth`)
- `POST /api/v1/auth/register`: Register new user and auto-generate Novice player profile.
- `POST /api/v1/auth/login`: Authenticate credentials, return JWT accessToken + refreshToken.
- `POST /api/v1/auth/refresh`: Rotate refresh token and issue new accessToken.
- `GET /api/v1/auth/me`: Retrieve currently authenticated user profile, rank, and progression.
- `PUT /api/v1/auth/profile`: Update bio, avatar, or display name.
- `POST /api/v1/auth/logout`: Revoke active session tokens.

### 3.2 Challenges & Problem Catalog (`/api/v1/challenges`)
- `GET /api/v1/challenges`: List challenges with optional filters (`difficulty`, `category`, `search`, pagination).
- `GET /api/v1/challenges/{slug}`: Fetch challenge details, specifications, public test cases, and starter code stubs.
- `GET /api/v1/challenges/categories`: List problem categories and counts.

### 3.3 Code Execution & Submissions (`/api/v1/code`, `/api/v1/submissions`)
- `POST /api/v1/code/run`: Execute untrusted code in isolated sandbox against test inputs without persisting submission.
- `POST /api/v1/submissions`: Submit formal solution for evaluation against full test suites. On success: awards XP, triggers quest progress, updates skill mastery, and checks achievements.
- `GET /api/v1/submissions/{id}`: Fetch submission result with test case breakdown.
- `GET /api/v1/submissions/my`: Get current player's submission history.
- `GET /api/v1/submissions/challenge/{slug}`: Get player's submissions for specific challenge.

### 3.4 Matchmaking & 1v1 Battle Arena (`/api/v1/matchmaking`, `/api/v1/battles`)
- `POST /api/v1/matchmaking/join`: Enter matchmaking queue with MMR rating.
- `POST /api/v1/matchmaking/leave`: Leave matchmaking queue.
- `GET /api/v1/matchmaking/status`: Poll current queue status.
- `GET /api/v1/battles/{id}`: Get real-time battle room state.
- `POST /api/v1/battles/{id}/ready`: Mark player ready in battle room.
- `POST /api/v1/battles/{id}/submit`: Submit solution in active battle; server validates tests and resolves match outcome.
- `POST /api/v1/battles/{id}/forfeit`: Forfeit match, awarding victory to opponent.
- `POST /api/v1/battles/{id}/sync`: Sync keystroke/editor stats with opponent.
- `GET /api/v1/battles/my`: Get battle history for current player.
- `GET /api/v1/battles/active`: Get active battle room if reconnecting.

### 3.5 Rankings, Achievements, Skills & Analytics
- `GET /api/v1/leaderboard/global`: Global ladder ranked by MMR with seasonal tier badges.
- `GET /api/v1/leaderboard/friends`: Leaderboard filtered to player's accepted friends.
- `GET /api/v1/achievements`: All platform achievements with criteria.
- `GET /api/v1/achievements/my`: Current player's unlocked achievements.
- `GET /api/v1/skills`: Skill tree definitions (Arrays, Algorithms, DP, Graphs, etc.).
- `GET /api/v1/skills/my`: Player's current mastery levels and XP per skill node.
- `GET /api/v1/analytics/overview`: High-level player radar metrics, win rates, and solve counts.
- `GET /api/v1/quests/daily`: Current daily quests with progression and rewards.

### 3.6 Social, Teams & Notifications (`/api/v1/friends`, `/api/v1/teams`, `/api/v1/notifications`)
- `GET /api/v1/players/search?q=`: Discover players by username or display name.
- `GET /api/v1/players/{username}`: View public player profile and stats.
- `POST /api/v1/friends/request`: Send friend request to player ID.
- `POST /api/v1/friends/accept/{id}`: Accept incoming friend request.
- `POST /api/v1/friends/decline/{id}`: Decline incoming friend request.
- `GET /api/v1/friends`: List accepted friends with online presence.
- `GET /api/v1/friends/requests/pending`: List pending incoming and outgoing requests.
- `GET /api/v1/teams`: List team guilds.
- `POST /api/v1/teams`: Create new team guild.
- `GET /api/v1/teams/{id}`: Get team roster, total rating, and statistics.
- `POST /api/v1/teams/{id}/join`: Join open team guild.
- `POST /api/v1/teams/{id}/leave`: Leave current team guild.
- `GET /api/v1/notifications`: List player notifications.
- `GET /api/v1/notifications/unread-count`: Fast unread count badge counter.
- `POST /api/v1/notifications/{id}/read`: Mark single notification read.
- `POST /api/v1/notifications/read-all`: Mark all notifications read.

### 3.7 AI Coach (`/api/v1/ai`)
- `POST /api/v1/ai/advice`: Request contextual Socratic advice, complexity tips, or conceptual nudges for a challenge without spoiling code.
- `POST /api/v1/ai/explain-failure`: Analyze compiler/runtime error or failing test case and explain underlying root cause.
- `GET /api/v1/ai/learning-path`: Generate personalized next challenges based on skill gaps.

### 3.8 Admin Command HQ (`/api/v1/admin`) — Requires `ROLE_ADMIN`
- `GET /api/v1/admin/overview`: Platform operational dashboard metrics (active players, battles, submissions, reports).
- `GET /api/v1/admin/players`: Paginated player management list.
- `POST /api/v1/admin/players/{id}/ban`: Suspend offending player account.
- `POST /api/v1/admin/players/{id}/unban`: Restore player account.
- `GET /api/v1/admin/reports`: List player conduct reports with statuses.
- `POST /api/v1/admin/reports/{id}/resolve`: Resolve report with admin action.
- `GET /api/v1/admin/integrity/flags`: Anti-cheat suspicious pattern alerts (rapid submissions, copy-paste heuristics).
- `GET /api/v1/admin/audit-logs`: Chronological administrative audit logs.
