# CRM Lite — Backend

Spring Boot REST API powering the CRM Lite application. Handles customer, contact, lead, and task management, notes, authentication, AI chat, and dashboard analytics.

> **Frontend Repo:** [github.com/Prachi088/crm-frontend](https://github.com/Prachi088/crm-frontend)
> **Live API:** [crm-backend-8ir9.onrender.com](https://crm-backend-8ir9.onrender.com)
>
> ⚠️ Hosted on Render's free tier, which spins down after inactivity. The first request after idle time may take **30–60 seconds** to wake up — this is expected, not a bug.

---

## Features

- **Customer & Contact Management** — Full CRUD for customers and their associated contacts, with search and pagination
- **Lead CRUD** — Create, read, update, and delete leads with name, email, company, deal value, pipeline status, priority, and expected revenue; cached read path for `GET /api/leads`
- **Task Management** — CRUD for tasks with due dates, priority, status, and links to customers/leads; dedicated "upcoming tasks" endpoint
- **Notes** — Add, edit, and delete notes attached to leads or customers; only the note's creator can edit/delete it
- **Auth** — JWT-based register and login with Spring Security, BCrypt password hashing, and role-based access control (`ADMIN`, `MANAGER`, `SALES_REPRESENTATIVE`)
- **AI Chat** — Chat endpoint powered by Groq AI (Llama 3.1) via `ChatService`
- **Dashboard Analytics** — Aggregated summary endpoint (totals, revenue, lead status breakdown, monthly trends, recent activity)
- **User Management** — Profile viewing/editing and a public-facing user directory (for assigning leads/tasks)
- **CORS** — Configured for the frontend origin via `CorsConfig`, driven by the `CORS_ORIGINS` environment variable
- **Redis Caching (optional)** — Lead list reads are cached via Redis when available; if Redis is unreachable, a custom `CacheErrorHandler` in `RedisConfig` catches the failure and falls back to direct database reads — the app never crashes or 500s because of a missing Redis instance
- **Health Check** — `/health` endpoint for deployment monitoring

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.5 |
| Language | Java 21 |
| Security | Spring Security + JWT (jjwt) |
| Database | PostgreSQL (Neon, serverless) |
| ORM | Spring Data JPA / Hibernate |
| Caching | Redis (optional, graceful fallback) |
| AI | Groq AI — Llama 3.1 8B (via `ChatService`) |
| Build | Maven |
| Deployment | Render (Docker) |

---

## Project Structure

```
src/main/java/com/crm/crm_lite/
├── config/
│   ├── CorsConfig.java             # CORS configuration (env-driven allowed origins)
│   └── RedisConfig.java            # Redis cache manager + graceful fallback error handler
├── controller/
│   ├── AuthController.java         # Register / login endpoints
│   ├── ChatController.java         # AI chat endpoint
│   ├── ContactController.java      # Contact CRUD, scoped to a customer
│   ├── ContactSearchController.java# Flat, cross-customer contact search
│   ├── CustomerController.java     # Customer CRUD + search
│   ├── CustomerNoteController.java # Notes scoped to a customer
│   ├── DashboardController.java    # Dashboard summary + activity feed
│   ├── HealthController.java       # Health check endpoint
│   ├── LeadController.java         # Lead CRUD + paginated search
│   ├── NoteController.java         # Notes scoped to a lead
│   ├── TaskController.java         # Task CRUD, status updates, upcoming tasks
│   └── UserController.java         # User profile + directory endpoints
├── dto/
│   ├── AuthRequest.java / AuthResponse.java
│   ├── ContactDto.java / CustomerDto.java / NoteDto.java / TaskDto.java
│   ├── DashboardSummaryResponse.java
│   └── PagedLeadsResponse.java
├── exception/
│   └── GlobalExceptionHandler.java # Centralised error responses (validation, 404s, 500s)
├── model/
│   ├── Contact.java / Customer.java / Lead.java / Note.java / Task.java / User.java
│   └── ChatRequest.java
├── repository/
│   ├── ContactRepository.java / CustomerRepository.java / LeadRepository.java
│   ├── NoteRepository.java / TaskRepository.java / UserRepository.java
├── security/
│   ├── JwtFilter.java               # JWT request filter
│   ├── JwtUtil.java                 # JWT generation and validation
│   └── SecurityConfig.java          # Spring Security filter chain + route rules
├── service/
│   ├── AuthService.java / ChatService.java / ContactService.java
│   ├── CustomerNoteService.java / CustomerService.java / DashboardService.java
│   ├── LeadService.java / NoteService.java / TaskService.java / UserService.java
└── CrmLiteApplication.java          # Entry point
```

---

## Getting Started

### Prerequisites

- **Java 21** (matches `pom.xml`'s `java.version` — Java 17 will fail to compile)
- Maven (or use the included `mvnw` wrapper)
- PostgreSQL database (local, or [Neon](https://neon.tech) for serverless Postgres)
- Redis (optional — the app runs fine without it; see [Caching](#caching) below)

### Installation

```bash
git clone https://github.com/Prachi088/crm-backend.git
cd crm-backend
```

### Environment Variables

The app reads all config from environment variables, with local-dev-friendly defaults baked into `application.properties`. For local development, create a `.env` file (not committed) or set these directly:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=crmdb
DB_USER=postgres
DB_PASSWORD=your_local_postgres_password
DB_SSLMODE=disable

# Auth
JWT_SECRET=any_long_random_string_for_local_dev

# AI Chat (optional — chat endpoint degrades gracefully without it)
GROQ_API_KEY=your_groq_api_key

# CORS — comma-separated if multiple origins
CORS_ORIGINS=http://localhost:3000

# Redis (optional — leave unset to run without caching)
REDIS_HOST=localhost
REDIS_PORT=6379
```

For **production** (e.g. Render + Neon), the equivalent variables are:

```env
DB_HOST=<your-neon-host>.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USER=<neon-username>
DB_PASSWORD=<neon-password>
DB_SSLMODE=require
JWT_SECRET=<a real random secret — generate with: openssl rand -base64 48>
GROQ_API_KEY=<your-groq-key>
CORS_ORIGINS=https://your-frontend-url.onrender.com
```

> **Neon note:** the database name is `neondb` by default — this is different from your Neon *project* name (which may be something else, shown in the console sidebar). Double-check `DB_NAME` matches the actual database, not the project.

### Run Locally

```bash
./mvnw spring-boot:run
```

API runs at `http://localhost:8080`. Confirm it's up:
```bash
curl http://localhost:8080/health
# → OK
```

### Build

```bash
./mvnw clean package -DskipTests
```

### Docker

A `Dockerfile` is included for containerized builds/deploys (multi-stage: Maven build → JRE runtime, both on Java 21):

```bash
docker build -t crm-backend .
docker run -p 8080:8080 --env-file .env crm-backend
```

---

## Caching

Lead list reads (`GET /api/leads`) are cached in Redis with a 10-minute TTL, invalidated on any lead/note create, update, or delete. **Redis is not required to run this app.** `RedisConfig.java` implements a `CacheErrorHandler` that catches any Redis connection failure, logs a warning, and lets the request fall through to a direct database read — so the app works identically with or without Redis configured, just without the caching speed benefit.

To enable real caching, set `REDIS_HOST` / `REDIS_PORT` (and `REDIS_PASSWORD` / `REDIS_SSL=true` if your provider requires auth/TLS — e.g. Upstash, Render's Key Value free tier).

---

## Authentication & Roles

- `POST /api/auth/register` and `POST /api/auth/login` return a JWT in the response body (not a cookie) — the frontend is responsible for storing it and attaching `Authorization: Bearer <token>` to subsequent requests.
- Three roles exist: `ADMIN`, `MANAGER`, `SALES_REPRESENTATIVE` (default for new registrations).
- The public registration endpoint does **not** expose role selection to arbitrary users by design — new accounts default to `SALES_REPRESENTATIVE`. Promoting a user to `ADMIN` currently requires a direct database update:
  ```sql
  UPDATE users SET role = 'ADMIN' WHERE email = 'someone@example.com';
  ```
  (The user must log out and back in afterward, since the role is baked into the JWT at login time.)
- Sales Representatives can only edit/delete leads they own; Admins and Managers can act on any lead.

---

## API Endpoints

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register a new user |
| POST | `/api/auth/login` | No | Login and receive JWT |
| GET | `/api/leads` | No | Get all leads (cached) |
| GET | `/api/leads/search` | No | Paginated, searchable leads |
| GET | `/api/leads/{id}` | No | Get a single lead |
| POST | `/api/leads` | Yes | Create a lead |
| PUT | `/api/leads/{id}` | Yes (owner or Admin/Manager) | Update a lead |
| DELETE | `/api/leads/{id}` | Yes (owner or Admin/Manager) | Delete a lead |
| GET | `/api/notes/lead/{leadId}` | No | Get notes for a lead |
| POST | `/api/notes/lead/{leadId}` | Yes | Add a note to a lead |
| PUT | `/api/notes/{id}` | Yes (note creator) | Edit a note |
| DELETE | `/api/notes/{id}` | Yes (note creator) | Delete a note |
| GET | `/api/customers` | Yes | Search/list customers |
| GET / POST / PUT / DELETE | `/api/customers/{id}` | Yes | Customer CRUD |
| GET / POST / PUT / DELETE | `/api/customers/{id}/contacts` | Yes | Contacts scoped to a customer |
| GET | `/api/contacts` | Yes | Flat, cross-customer contact search |
| GET | `/api/tasks` | Yes | Search/list tasks |
| GET | `/api/tasks/upcoming` | Yes | Next N incomplete tasks by due date |
| POST / PUT / DELETE | `/api/tasks/{id}` | Yes | Task CRUD |
| PATCH | `/api/tasks/{id}/status` | Yes | Quick status update |
| GET | `/api/dashboard/summary` | Yes | Aggregated dashboard metrics |
| GET | `/api/dashboard/activities` | Yes | Recent activity feed |
| POST | `/api/chat` | No | Send a message to the AI assistant |
| GET | `/api/users` | Yes | List all users (for assignment dropdowns) |
| GET | `/api/users/me` | Yes | Current user's profile |
| GET | `/api/users/{id}` | Yes | Another user's public profile |
| PUT | `/api/users/me` | Yes | Update own password |
| GET | `/health` | No | Health check |

---

## Known Limitations

- Admin role assignment requires a manual database update — there is no admin-promotion UI or endpoint yet.
- Redis caching is optional and stateless-safe, but on Render's free "Key Value" tier, cached data does not persist across instance restarts (acceptable here since the cache simply repopulates from the database).
- Free-tier hosting (Render + Neon) means occasional cold starts: the backend sleeps after 15 minutes of inactivity, and Neon auto-suspends its compute after a period of idleness. Both wake automatically on the next request, at the cost of a slower first response.

---

## Author

**Prachi Rajput**
[GitHub](https://github.com/Prachi088) · [LinkedIn](https://linkedin.com/in/prachi-rajput-023985280)