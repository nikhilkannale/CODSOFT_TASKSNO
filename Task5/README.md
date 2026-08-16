# 🎓 Student Course Registration System

A complete, production-ready **Student Course Registration System** built for **CodSoft Java Development Internship – Task 5**. Students can browse courses, register/drop with real-time seat validation, and manage their profile; admins get a full dashboard to manage courses, students, and registrations.

Built with **Java 21, Spring Boot 3, Spring Security (JWT + session), Spring Data JPA, PostgreSQL, and Thymeleaf + Bootstrap 5.**

> ⚠️ **A note on this deliverable:** this code was generated in a sandboxed environment without access to Maven Central, so it could not be `mvn`-compiled here to give you a 100% verified green build. Every file was written carefully and reviewed by hand for consistency (imports, method signatures, Thymeleaf bindings, security wiring). Please run `mvn clean install` as your first step after cloning — see [Troubleshooting](#-troubleshooting) if anything needs a tweak.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema / ER Diagram](#-database-schema--er-diagram)
- [Getting Started (Local Setup)](#-getting-started-local-setup)
- [Running with Docker](#-running-with-docker)
- [Default Accounts](#-default-accounts)
- [API Documentation](#-api-documentation)
- [Deployment Guide (Render + Neon)](#-deployment-guide-render--neon)
- [Testing](#-testing)
- [Troubleshooting](#-troubleshooting)
- [Screenshots](#-screenshots)

---

## ✨ Features

**Students**
- Register, log in, log out
- Browse all courses with live available-seat counts
- Search/filter courses by keyword, department, semester
- View course details and register (blocked automatically once a course is full)
- Drop a registered course (seat is released back to the pool)
- View "My Courses" and dashboard stats
- Edit profile, change password, upload a profile picture

**Admins**
- Dashboard with total students / courses / active registrations / available seats
- Add, edit, delete courses
- Increase/decrease course capacity on the fly
- View and delete students
- View all registrations and force-drop any registration

**Cross-cutting**
- BCrypt password hashing
- JWT authentication for the REST API + session auth for the web UI (same user store)
- Role-based authorization (`STUDENT`, `ADMIN`)
- Centralized exception handling with consistent JSON error responses
- Bean Validation on every request DTO
- Swagger / OpenAPI docs at `/swagger-ui.html`
- JUnit 5 tests covering the core registration business rules
- Dockerfile + docker-compose for one-command local spin-up
- GitHub Actions CI pipeline

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3, Spring MVC, Spring Data JPA, Spring Security |
| Auth | JWT (`jjwt`) for API, session/form-login for the web UI, BCrypt |
| Database | PostgreSQL (Neon in production), H2 for tests |
| Templating | Thymeleaf + Bootstrap 5 + Font Awesome |
| Build | Maven |
| Docs | springdoc-openapi (Swagger UI) |
| Containerization | Docker, Docker Compose |
| CI | GitHub Actions |

---

## 📁 Project Structure

```
StudentCourseRegistrationSystem/
├── src/main/java/com/codsoft/scrs/
│   ├── controller/
│   │   ├── api/            REST controllers (/api/**, JWT-secured)
│   │   └── web/             Thymeleaf controllers (session-secured)
│   ├── service/              Business logic (registration rules live here)
│   ├── repository/           Spring Data JPA repositories
│   ├── entity/                JPA entities: Student, Course, Enrollment
│   ├── dto/                   Request/response DTOs
│   ├── security/              JWT filter, UserDetails adapter, JwtUtil
│   ├── config/                Security config, OpenAPI config, static resources
│   ├── exception/             Custom exceptions + GlobalExceptionHandler
│   └── StudentCourseRegistrationApplication.java
├── src/main/resources/
│   ├── templates/             Thymeleaf pages (home, auth, student/*, admin/*)
│   ├── static/{css,js}
│   ├── application.properties
│   └── data.sql               Seeds one admin account
├── src/test/java/...           JUnit 5 tests (H2 in-memory)
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── .github/workflows/ci.yml
```

---

## 🗄 Database Schema / ER Diagram

**Entities**

```
Student                       Course                        Enrollment
─────────────────────         ─────────────────────         ─────────────────────
id (PK)                       id (PK)                       id (PK)
studentId (unique)            courseCode (unique)           student_id (FK -> Student)
fullName                      title                         course_id (FK -> Course)
email (unique)                description                   enrollmentDate
password (BCrypt)             instructor                    status (ACTIVE/DROPPED/WAITLISTED)
department                    department
semester                      semester
profilePictureUrl             capacity
role (STUDENT/ADMIN)          availableSeats
createdAt                     schedule
```

**Relationships**
- `Student 1 ── * Enrollment` (one student has many enrollments)
- `Course  1 ── * Enrollment` (one course has many enrollments)
- `Enrollment` is the join entity, with a unique constraint on `(student_id, course_id)` so the database itself guarantees a student can't be double-enrolled in the same course.

```
 ┌───────────┐        ┌──────────────┐        ┌───────────┐
 │  Student  │1      *│  Enrollment  │*      1│   Course  │
 ├───────────┤◄───────┼──────────────┼───────►├───────────┤
 │ id        │        │ id           │        │ id        │
 │ studentId │        │ student_id   │        │ courseCode│
 │ email     │        │ course_id    │        │ title     │
 │ password  │        │ status       │        │ capacity  │
 │ role      │        │ enrollDate   │        │ availSeats│
 └───────────┘        └──────────────┘        └───────────┘
```

---

## 🚀 Getting Started (Local Setup)

### Prerequisites
- Java 21 (JDK)
- Maven 3.8+
- PostgreSQL 14+ running locally (or use Docker Compose — see below)

### 1. Clone and configure

```bash
git clone https://github.com/<your-username>/StudentCourseRegistrationSystem.git
cd StudentCourseRegistrationSystem
```

Create a database:

```sql
CREATE DATABASE scrs_db;
```

By default the app reads connection info from environment variables with local fallbacks:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/scrs_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
export JWT_SECRET=ChangeThisSecretKeyInProductionEnvironmentVariableAtLeast256BitsLong123456
```

(On Windows PowerShell use `$env:DATABASE_URL = "..."` etc. Or just edit `src/main/resources/application.properties` directly for local dev.)

### 2. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

The app starts on **http://localhost:8080**. On first boot, Hibernate creates the schema and `data.sql` seeds one admin account (see [Default Accounts](#-default-accounts)).

### 3. Open it

- Website: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## 🐳 Running with Docker

The fastest way to get a full stack (app + Postgres) running:

```bash
docker compose up --build
```

This starts:
- `db` — Postgres 16, persisted in a named volume
- `app` — the Spring Boot app, built from the included `Dockerfile`, listening on port 8080

Then open http://localhost:8080.

To stop: `docker compose down` (add `-v` to also wipe the database volume).

---

## 🔑 Default Accounts

| Role | Email | Password |
|---|---|---|
| Admin | `admin@scrs.com` | `Admin@123` |
| Student | Register your own via **Sign Up** | — |

*(Seeded by `data.sql` with a BCrypt hash — change this password immediately in any real deployment.)*

---

## 📡 API Documentation

Full interactive docs (with request/response schemas) are available at **`/swagger-ui.html`** once the app is running. Summary:

### Auth (`/api/auth`) — public
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create a student account, returns a JWT |
| POST | `/api/auth/login` | Authenticate, returns a JWT |

### Courses (`/api/courses`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/courses` | public | List/search courses (`?keyword=&department=&semester=`) |
| GET | `/api/courses/{id}` | public | Get one course |
| POST | `/api/courses` | ADMIN | Create a course |
| PUT | `/api/courses/{id}` | ADMIN | Update a course |
| PATCH | `/api/courses/{id}/capacity?delta=` | ADMIN | Increase/decrease capacity |
| DELETE | `/api/courses/{id}` | ADMIN | Delete a course |

### Students (`/api/students`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/students` | ADMIN | List all students |
| GET | `/api/students/{id}` | self or ADMIN | Get a profile |
| PUT | `/api/students/{id}` | self or ADMIN | Update a profile |
| POST | `/api/students/{id}/change-password` | self | Change password |
| DELETE | `/api/students/{id}` | ADMIN | Delete a student |
| GET | `/api/students/me` | authenticated | Get the caller's own profile |
| GET | `/api/students/dashboard-stats` | ADMIN | Aggregate dashboard numbers |

### Enrollments (`/api/enrollments`)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/enrollments` | authenticated | Register for a course (`{"courseId": 1}`) |
| DELETE | `/api/enrollments/{id}` | owner or ADMIN | Drop a registration |
| GET | `/api/enrollments/student/{studentId}` | owner or ADMIN | List a student's registrations |
| GET | `/api/enrollments` | ADMIN | List every registration |

### Authenticating API calls

```bash
# 1. Log in to get a token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@scrs.com","password":"Admin@123"}'

# 2. Use the token
curl http://localhost:8080/api/students/me \
  -H "Authorization: Bearer <token>"
```

### Error format

Every error (validation, not-found, capacity-full, duplicate, unauthorized, etc.) returns:

```json
{
  "timestamp": "2026-08-16 10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "'Data Structures' is at full capacity. No seats are currently available.",
  "path": "/api/enrollments",
  "fieldErrors": null
}
```

---

## ☁️ Deployment Guide (Render + Neon)

### 1. Provision the database (Neon)
1. Create a free project at [neon.tech](https://neon.tech).
2. Copy the connection string it gives you (looks like `postgresql://user:pass@ep-xxxx.neon.tech/dbname?sslmode=require`).
3. Convert it to a JDBC URL: `jdbc:postgresql://ep-xxxx.neon.tech/dbname?sslmode=require`.

### 2. Deploy the app (Render)
1. Push this repository to GitHub.
2. In Render, create a **New Web Service** → connect your repo.
3. Environment: **Docker** (Render will build from the included `Dockerfile`), or use a **Java** environment with:
   - Build command: `mvn clean package -DskipTests`
   - Start command: `java -jar target/student-course-registration-system.jar`
4. Set environment variables in Render's dashboard:

   | Key | Value |
   |---|---|
   | `DATABASE_URL` | `jdbc:postgresql://ep-xxxx.neon.tech/dbname?sslmode=require` |
   | `DATABASE_USERNAME` | your Neon username |
   | `DATABASE_PASSWORD` | your Neon password |
   | `JWT_SECRET` | a long random string (256-bit+) |
   | `JWT_EXPIRATION_MS` | `86400000` |
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `PORT` | `8080` (Render sets/reads this automatically) |

5. Deploy. Render will build the image, run the app, and give you a public URL like `https://scrs.onrender.com`.

### 3. Verify
- Visit the Render URL → home page should load.
- Log in with the seeded admin account (or register a student).
- Check `/swagger-ui.html` on the deployed URL to confirm the API is live.

---

## 🧪 Testing

Tests run against an in-memory H2 database (no Postgres needed) via `src/test/resources/application.properties`.

```bash
mvn test
```

Included tests:
- `StudentCourseRegistrationApplicationTests` — verifies the full Spring context (security, JPA, MVC) boots correctly.
- `EnrollmentServiceTest` — exercises the core rules: registering decrements seats, duplicate registration is rejected, registering into a full course is rejected, dropping restores the seat.

---

## 🩺 Troubleshooting

- **`Non-resolvable parent POM` / dependency download errors** — your build machine needs outbound internet access to Maven Central (`repo.maven.apache.org`). Corporate proxies/firewalls sometimes block this; configure your Maven `settings.xml` mirror accordingly.
- **`FATAL: password authentication failed` on startup** — double-check `DATABASE_URL` / `DATABASE_USERNAME` / `DATABASE_PASSWORD` match your Postgres instance.
- **Login redirects to `/login?error=true`** — confirm you're using the account's **email**, not student ID, and that the password is correct (case-sensitive).
- **File upload fails** — make sure the `uploads/profile-pictures` directory is writable (Docker image creates it automatically; locally it's created on first upload).
- **Lombok "cannot find symbol" errors in your IDE** — install the Lombok plugin for IntelliJ/VS Code and enable annotation processing.

---

## 📸 Screenshots

Add screenshots of the running application here (home page, dashboards, course list, registration flow) once deployed — see the `screenshots/` folder.

---

## 📄 License

Built for educational purposes as part of the CodSoft Java Development Internship. Free to use and adapt for your own portfolio.

