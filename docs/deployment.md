# DevArena Production Deployment Guide

This guide provides step-by-step instructions for deploying DevArena into production environments using Docker Compose, Standalone VPS, Kubernetes, or Cloud Managed Platforms (Render, Railway, AWS ECS, DigitalOcean).

---

## 1. Prerequisites

Ensure your target hosting environment meets the following specifications:

- **Host Machine / VM**:
  - 2+ vCPUs, 4GB+ RAM (8GB recommended for concurrent battle sandboxes)
  - 20GB+ SSD Storage
  - Ubuntu 22.04 LTS / Debian 12 / Alpine Linux
- **Software**:
  - Docker Engine >= 24.0
  - Docker Compose v2 >= 2.20
  - Optional: Git, OpenSSL, Nginx / Caddy, Certbot

---

## 2. Docker Compose Deployment (Recommended for VPS)

Docker Compose orchestrates PostgreSQL 16, Redis 7, Spring Boot Backend (Java 21), and the Nginx Frontend reverse proxy in a private bridged network.

### Step 1: Clone Repository
```bash
git clone https://github.com/ARUN-1-tech/DevArena.git /opt/devarena
cd /opt/devarena
```

### Step 2: Configure Environment Variables
```bash
cp .env.example .env
```

Generate secure production secrets:
```bash
# Generate a 512-bit JWT Secret Key
openssl rand -base64 64

# Generate a strong PostgreSQL password
openssl rand -hex 24
```

Edit `.env` and set production values:
```ini
APP_ENV=production
SERVER_PORT=8080
FRONTEND_PORT=80
CORS_ALLOWED_ORIGINS=https://devarena.yourdomain.com,http://localhost:3000

POSTGRES_DB=devarena
POSTGRES_USER=devarena_user
POSTGRES_PASSWORD=<YOUR_STRONG_POSTGRES_PASSWORD>
POSTGRES_HOST=postgres
POSTGRES_PORT=5432

REDIS_HOST=redis
REDIS_PORT=6379

JWT_SECRET=<YOUR_GENERATED_JWT_SECRET>
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000

EXECUTION_TIMEOUT_MS=5000
EXECUTION_SANDBOX_ENABLED=true
```

### Step 3: Build and Launch Containers
```bash
docker compose up -d --build
```

### Step 4: Verify Container Health
```bash
docker compose ps
docker compose logs -f backend
```

Once initialized, Spring Boot executes Flyway migrations (`V1` to `V9`) and seeds default administrators and the 128+ Problem Archive dataset.

---

## 3. Production SSL / TLS Configuration (Let's Encrypt)

If hosting on a public domain (e.g. `devarena.yourdomain.com`):

### Option A: Certbot + Host Nginx Reverse Proxy
Install Nginx and Certbot on your host:
```bash
sudo apt update && sudo apt install -y nginx certbot python3-certbot-nginx
```

Configure `/etc/nginx/sites-available/devarena`:
```nginx
server {
    server_name devarena.yourdomain.com;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /ws {
        proxy_pass http://127.0.0.1:8080/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 86400s;
        proxy_send_timeout 86400s;
    }
}
```

Enable site and acquire SSL certificate:
```bash
sudo ln -s /etc/nginx/sites-available/devarena /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d devarena.yourdomain.com
```

---

## 4. Cloud Platform Deployments (Render / Railway / Fly.io)

### Render / Railway
1. Connect your GitHub repository `DevArena`.
2. **PostgreSQL**: Create a Managed PostgreSQL service. Copy the connection string to `SPRING_DATASOURCE_URL`.
3. **Redis**: Create a Managed Redis service. Set `SPRING_DATA_REDIS_HOST` and `SPRING_DATA_REDIS_PORT`.
4. **Backend Service**:
   - Dockerfile path: `docker/backend/Dockerfile` (or root `Dockerfile`)
   - Environment variables: Set `SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET`, `SPRING_DATASOURCE_URL`, `SPRING_DATA_REDIS_HOST`.
5. **Frontend Service**:
   - Dockerfile path: `docker/frontend/Dockerfile`
   - Set `VITE_API_BASE_URL` to backend service URL.

---

## 5. Maintenance, Backups & Monitoring

### Database Backups
```bash
# Create automated daily backup
docker exec -t devarena-postgres pg_dump -U devarena_user devarena > /opt/backups/devarena_$(date +\%F).sql
```

### Application Logs
```bash
docker compose logs -f --tail=100 backend
docker compose logs -f --tail=100 frontend
```

### Zero-Downtime Rolling Update
```bash
git pull origin main
docker compose build backend frontend
docker compose up -d --no-deps backend frontend
```
