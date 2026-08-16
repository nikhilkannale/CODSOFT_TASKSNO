
# 📘 Quiz Application with Timer

A full-stack, timed quiz platform built for **CodSoft Java Development Internship — Task 4**.
Students register, take a countdown-timed quiz one question at a time, and get an instant,
detailed score breakdown. Admins create quizzes and questions, manage users, and review results
and leaderboards.

**Stack:** Java 21 · Spring Boot 3 · Spring Security (JWT) · Spring Data JPA / Hibernate ·
Thymeleaf · Bootstrap 5 · PostgreSQL

---

## Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Project structure](#project-structure)
- [Quick start](#quick-start)
- [Configuration](#configuration)
- [Default accounts](#default-accounts)
- [Documentation](#documentation)
- [Running tests](#running-tests)
- [Tech notes / design decisions](#tech-notes--design-decisions)

---

## Features

**Students**
- Register / log in (BCrypt-hashed passwords, JWT session)
- Browse active quizzes
- Take a quiz one question at a time with a live countdown timer
- Timer survives a page refresh and auto-submits the instant it hits zero
- Instant results: score, percentage, correct/incorrect counts, pass/fail, time taken
- View history of past attempts
- Global and per-quiz leaderboards

**Admins**
- Create, edit, activate/deactivate, and delete quizzes
- Add, edit, and delete questions (4 options, one correct answer, configurable marks)
- View all registered users
- View every submitted result
- View leaderboards

**Engineering**
- Server-side grading — the client never receives correct answers before submission
- Role-based access control (`ADMIN` / `STUDENT`) enforced by Spring Security
- JWT carried via an HttpOnly cookie (works for both the REST API and the server-rendered UI)
- Centralized validation and error handling with meaningful JSON error responses
- Dockerized, with a docker-compose stack (app + PostgreSQL) for one-command local startup

---

## Screenshots

> Run the app locally (see [Quick start](#quick-start)) and drop screenshots into `/screenshots`.
> Suggested set: landing page, quiz-taking screen (timer visible), result page, admin quiz
> management, leaderboard.

| | |
|---|---|
| ![Dashboard](screenshots/dashboard.png) | ![Quiz](screenshots/quiz-take.png) |
| ![Result](screenshots/result.png) | ![Admin](screenshots/admin-quizzes.png) |

---

## Project structure

```text
QuizApplication/
├── src/main/java/com/codsoft/quizapp/
│   ├── controller/     REST controllers + Thymeleaf page controller
│   ├── service/        Business logic (auth, quizzes, grading, leaderboard, users)
│   ├── repository/     Spring Data JPA repositories
│   ├── entity/         JPA entities: User, Quiz, Question, Result, Role
│   ├── dto/             Request/response DTOs
│   ├── security/       JWT service, filter, UserDetails adapter
│   ├── config/         Spring Security config, OpenAPI config, data seeder
│   ├── exception/      Custom exceptions + global handler
│   └── QuizApplication.java
├── src/main/resources/
│   ├── templates/       Thymeleaf pages (+ admin/ subfolder)
│   ├── static/css/js/   Custom design system + client-side logic
│   ├── application*.properties
│   └── data.sql          Reference seed data (not auto-run — see note in file)
├── src/test/java/...     Unit tests (grading logic, auth) + Spring context test
├── docs/                 API reference, ER diagram, setup & deployment guides
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Quick start

### Option A — Docker Compose (recommended, zero local setup)

Requires Docker only.

```bash
docker compose up --build
```

Open **http://localhost:8080**. A PostgreSQL container is provisioned automatically and the app
seeds a default admin account and a sample quiz on first boot (see
[Default accounts](#default-accounts)).

### Option B — Local Maven + your own PostgreSQL

1. Create a database:
   ```sql
   CREATE DATABASE quizapp;
   ```
2. Set environment variables (or edit `src/main/resources/application.properties` directly):
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/quizapp
   export DB_USERNAME=postgres
   export DB_PASSWORD=postgres
   export JWT_SECRET=$(openssl rand -base64 48)
   ```
3. Run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Open **http://localhost:8080**.

### Option C — No database at all (in-memory H2, for quickly poking around)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
This uses an in-memory H2 database (data resets every restart) and exposes an H2 console at
`/h2-console`. Not for anything beyond local exploration.

Full step-by-step instructions: **[docs/SETUP.md](docs/SETUP.md)**.

---

## Configuration

All configuration is environment-variable driven — nothing sensitive is hardcoded. Key variables
(see `application.properties` for the complete list and defaults):

| Variable | Purpose | Default (dev only) |
|---|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection | local Postgres |
| `JWT_SECRET` | Signing key for JWTs — **override this in any real deployment** | insecure dev value |
| `JWT_EXPIRATION_MS` | Token lifetime | 86400000 (24h) |
| `SEED_DATA` | Seed a default admin + sample quiz on first boot | `true` |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Seeded admin credentials | `admin@quizapp.com` / `Admin@123` |
| `PORT` | HTTP port | 8080 |

> ⚠️ **Before deploying anywhere real:** generate a long random `JWT_SECRET`
> (`openssl rand -base64 48`), change the default admin password, and set `SEED_DATA=false`
> once your real data exists.

---

## Default accounts

On first boot (when `SEED_DATA=true`, the default), the app creates:

- **Admin:** `admin@quizapp.com` / `Admin@123`
- **Sample quiz:** "Java Fundamentals" (5 questions, 5-minute timer)

Register a new account through the UI to try the student flow.

---

## Documentation

- **[docs/API.md](docs/API.md)** — full REST API reference (all endpoints, request/response shapes, auth)
- **[docs/ER-DIAGRAM.md](docs/ER-DIAGRAM.md)** — database schema and entity-relationship diagram
- **[docs/SETUP.md](docs/SETUP.md)** — detailed local setup (Docker, Maven, IDE)
- **[docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)** — deploying to Render (backend) + Neon (PostgreSQL)
- **[docs/TESTING.md](docs/TESTING.md)** — running automated tests and manually testing each feature

---

## Running tests

```bash
./mvnw test
```

Covers server-side grading logic (the most important business rule in the app — the client is
never trusted for correct answers), registration/auth logic, and a full Spring context load
(security + JPA + MVC wiring) against an in-memory H2 database. See
[docs/TESTING.md](docs/TESTING.md) for the full manual test checklist too.

---

## Tech notes / design decisions

- **JWT in an HttpOnly cookie, not localStorage.** The same token authenticates both the REST
  API (`Authorization: Bearer`) and the Thymeleaf-rendered pages (cookie, sent automatically by
  the browser), so there's one auth mechanism instead of two, and the token isn't reachable from
  JavaScript (mitigates XSS token theft).
- **Grading happens entirely server-side.** `GET /api/quizzes/{id}/questions` never returns the
  correct answer; `POST /api/results/submit` re-fetches the real questions and grades against
  them, ignoring/clamping anything a tampered client might send (invalid option letters,
  question IDs from other quizzes, an inflated `timeTakenInSeconds`).
- **Refresh-resilient timer.** The quiz start time is persisted in `localStorage` (keyed by quiz
  ID) the moment the quiz begins, so a refresh recomputes remaining time from the real elapsed
  time instead of resetting the clock — closing the "keep refreshing to get more time" loophole.
- **`spring.jpa.open-in-view=false`.** Lazy associations are fetch-joined explicitly in
  repository queries where needed, rather than relying on the (generally discouraged)
  open-session-in-view pattern.
