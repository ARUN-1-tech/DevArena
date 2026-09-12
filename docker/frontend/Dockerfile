# Multi-stage Dockerfile for DevArena Frontend (React + Vite)
# Build Context: repository root (.)

# Stage 1: Build production static bundle
FROM node:20-alpine AS builder
WORKDIR /app

# Copy dependency manifests
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci || npm install

# Copy application sources and configuration
COPY frontend ./

# Compile TypeScript and bundle via Vite
RUN npm run build

# Stage 2: Production Nginx Server
FROM nginx:1.27-alpine AS runner
WORKDIR /usr/share/nginx/html

# Clean default nginx website
RUN rm -rf ./*

# Copy compiled assets from builder
COPY --from=builder /app/dist .

# Copy custom Nginx reverse proxy configuration
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
