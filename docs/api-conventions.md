# DevArena API Conventions

All HTTP REST endpoints follow uniform API contracts, status codes, error payloads, and URL patterns.

---

## 1. Base URL & Versioning

All REST resources are prefixed with the major API version:

```
/api/v1
```

Future modules will route under these conventions:
- `/api/v1/auth` - Registration, login, token refresh
- `/api/v1/users` - Profiles, preferences, friends
- `/api/v1/challenges` - Problem catalog, submissions, solutions
- `/api/v1/battles` - Battle room management, matchmaking
- `/api/v1/leaderboard` - Global and seasonal rankings
- `/api/v1/achievements` - Badge catalog, unlock progress

---

## 2. Standard Success Response Envelope

Successful API operations return a consistent JSON envelope wrapped in `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Resource retrieved successfully",
  "data": {
    "id": "c1f73b64-890a-4299-bbd3-1a22026ef3ab",
    "name": "DevArena Platform",
    "status": "HEALTHY"
  },
  "timestamp": "2026-09-11T09:30:00Z"
}
```

### TypeScript Definition
```typescript
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}
```

---

## 3. Standard Error Response Format

Errors are captured globally by `GlobalExceptionHandler` and return the following schema:

```json
{
  "timestamp": "2026-09-11T09:30:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Field 'username' must not be blank",
  "path": "/api/v1/users"
}
```

### Key Error Codes:
| Error Code | HTTP Status | Description |
| :--- | :--- | :--- |
| `VALIDATION_ERROR` | 400 Bad Request | Payload validation failed |
| `BAD_REQUEST` | 400 Bad Request | Malformed request or illegal parameters |
| `UNAUTHORIZED` | 401 Unauthorized | Missing or expired JWT authentication |
| `FORBIDDEN` | 403 Forbidden | Authenticated user lacks required permissions |
| `RESOURCE_NOT_FOUND` | 404 Not Found | Requested entity was not found |
| `CONFLICT` | 409 Conflict | State collision (e.g. duplicate username/email) |
| `INTERNAL_SERVER_ERROR`| 500 Internal Server Error | Unhandled server exception |

---

## 4. HTTP Method Guidelines

- `GET`: Safe, idempotent read operations.
- `POST`: Create a new resource or initiate non-idempotent operations (e.g., submit code).
- `PUT`: Complete idempotent replacement of an existing resource.
- `PATCH`: Partial update of an existing resource.
- `DELETE`: Remove a resource.

---

## 5. Pagination Standard

Paginated responses contain pagination metadata within the standard `ApiResponse` payload:

```json
{
  "success": true,
  "data": {
    "items": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "last": false
  },
  "timestamp": "2026-09-11T09:30:00Z"
}
```
