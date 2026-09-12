# Security Policy

DevArena takes the security of our real-time coding battle arena, multi-tenant execution sandboxes, and player data very seriously. This document outlines our vulnerability disclosure process and security architecture principles.

---

## 1. Supported Versions

We provide security updates and patches for the following versions:

| Version | Supported          | Security Maintenance Status |
| ------- | ------------------ | --------------------------- |
| 1.0.x   | :white_check_mark: | Active (Current Mainline)   |
| < 1.0   | :x:                | Deprecated                  |

---

## 2. Reporting a Vulnerability

If you discover a potential security vulnerability within DevArena, please **DO NOT** disclose it publicly via GitHub Issues, Discussions, or social media.

### Private Reporting Channels
- **Security Email**: security@devarena.io
- **Subject Format**: `[SECURITY VULNERABILITY] <Component/Module> - <Brief Summary>`

### What to Include in Your Report
To help us triage and resolve the issue quickly, please provide:
1. **Description**: A clear explanation of the vulnerability and its potential impact.
2. **Reproduction Steps**: Step-by-step instructions or Proof-of-Concept (PoC) code / HTTP payloads to reproduce the issue.
3. **Affected Components**: Specify whether the issue affects the Backend REST APIs, WebSocket STOMP broker, Sandbox Process Execution, Database, or Frontend client.
4. **Environment**: Operating system, runtime versions (Java 21, Node 20, Docker), and browser if applicable.
5. **Mitigation**: Any suggestions or workarounds you may have discovered.

---

## 3. Response & Resolution Process

1. **Initial Acknowledgment**: Our engineering team will acknowledge receipt of your vulnerability report within **48 hours**.
2. **Assessment & Confirmation**: We will investigate, confirm reproducibility, and assign a severity score within **5 business days**.
3. **Patch Development & Verification**: A fix will be developed in a private security branch and verified against automated sandbox security test suites.
4. **Public Release & Advisory**: The patch will be deployed and tagged in a release, accompanied by a public security advisory with responsible disclosure credits.

---

## 4. Key Security Architectural Safeguards

DevArena incorporates multi-tiered security defense-in-depth:

- **Sandbox Execution Isolation**:
  - Untrusted user code (Java, Python, JavaScript) is executed inside isolated sub-processes with strict CPU, memory (RAM), and timeout limits (default 5000ms).
  - Network sockets, file system writes outside temporary scratch directories, and OS shell fork bombs are restricted.
- **Authentication & Authorization**:
  - Stateless JWT token architecture with short-lived Access Tokens (24h) and secure Refresh Tokens (7d).
  - Passwords hashed using standard BCrypt with high work factor.
  - Role-Based Access Control (`ROLE_USER`, `ROLE_ADMIN`) enforced via Spring Security method-level annotations.
- **Data Integrity & Protection**:
  - PostgreSQL Flyway versioned migrations prevent schema drift.
  - Parameterized queries and Spring Data JPA prevent SQL Injection.
  - Strict Content Security Policy (CSP), CORS whitelisting, and secure Nginx reverse proxy headers (`X-Content-Type-Options`, `X-Frame-Options`, `X-XSS-Protection`).

---

Thank you for helping keep DevArena and its developer community safe!
