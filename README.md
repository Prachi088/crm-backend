# CRM Lite — Backend

Spring Boot REST API powering the CRM Lite application. Handles lead management, notes, authentication, AI chat, and CSV export.

> **Frontend Repo:** [github.com/Prachi088/crm-frontend](https://github.com/Prachi088/crm-frontend)
> **Live API:** Deployed on Render

---

## Features

- **Lead CRUD** — Create, read, update, and delete leads with name, email, company, deal value, and pipeline status
- **Notes** — Add and retrieve notes attached to individual leads
- **Auth** — JWT-based login and signup with Spring Security
- **AI Chat** — Chat endpoint powered by Groq AI via `ChatService`
- **User Management** — User profile and account handling
- **CSV Export** — Export all lead data (consumed by frontend Export CSV feature)
- **CORS** — Configured for frontend origin via `CorsConfig`
- **Health Check** — `/health` endpoint for deployment monitoring

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot |
| Language | Java |
| Security | Spring Security + JWT |
| Database | PostgreSQL (Neon) |
| ORM | Spring Data JPA |
| AI | Groq AI (ChatService) |
| Deployment | Render |

---

## Project Structure

```
crm-lite/
└── src/main/java/com/crm/crm_lite/
    ├── config/
    │   └── CorsConfig.java           # CORS configuration
    ├── controller/
    │   ├── AuthController.java       # Login / signup endpoints
    │   ├── ChatController.java       # AI chat endpoint
    │   ├── HealthController.java     # Health check endpoint
    │   ├── LeadController.java       # Lead CRUD endpoints
    │   ├── NoteController.java       # Notes endpoints
    │   └── UserController.java       # User profile endpoints
    ├── dto/
    │   ├── AuthRequest.java          # Login request body
    │   └── AuthResponse.java         # JWT response body
    ├── model/
    │   ├── ChatRequest.java          # AI chat request model
    │   ├── Lead.java                 # Lead entity
    │   ├── Note.java                 # Note entity
    │   └── User.java                 # User entity
    ├── repository/
    │   ├── LeadRepository.java       # Lead JPA repository
    │   ├── NoteRepository.java       # Note JPA repository
    │   └── UserRepository.java       # User JPA repository
    ├── security/
    │   ├── JwtFilter.java            # JWT request filter
    │   ├── JwtUtil.java              # JWT generation and validation
    │   └── SecurityConfig.java       # Spring Security configuration
    ├── service/
    │   ├── AuthService.java          # Auth business logic
    │   ├── ChatService.java          # Groq AI integration
    │   ├── LeadService.java          # Lead business logic
    │   ├── NoteService.java          # Notes business logic
    │   └── UserService.java          # User business logic
    └── CrmLiteApplication.java       # Entry point
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL database (local or [Neon](https://neon.tech))

### Installation

```bash
git clone https://github.com/Prachi088/crm-backend.git
cd crm-backend
```

### Environment Variables

Set the following in `application.properties` or as environment variables on Render:

```properties
spring.datasource.url=jdbc:postgresql://<host>/<db>
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
jwt.secret=<your_jwt_secret>
groq.api.key=<your_groq_api_key>
```

### Run Locally

```bash
./mvnw spring-boot:run
```

API runs at `http://localhost:8080`.

### Build

```bash
./mvnw clean package
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |
| GET | `/api/leads` | Get all leads |
| POST | `/api/leads` | Create a new lead |
| PUT | `/api/leads/{id}` | Update a lead |
| DELETE | `/api/leads/{id}` | Delete a lead |
| GET | `/api/leads/{id}/notes` | Get notes for a lead |
| POST | `/api/leads/{id}/notes` | Add a note to a lead |
| POST | `/api/chat` | Send a message to AI |
| GET | `/api/user/profile` | Get logged-in user profile |
| GET | `/health` | Health check |

---

## Author

**Prachi Rajput**
[GitHub](https://github.com/Prachi088) · [LinkedIn](https://linkedin.com/in/prachi-rajput-023985280)
