# Setup Guide

## Prerequisites

- Java 21 (JDK)
- Maven 3.9+ (or use the bundled `./mvnw` wrapper — no local Maven install needed)
- Docker + Docker Compose (for the containerized option)
- PostgreSQL 14+ (if not using Docker or the H2 dev profile)

Check your Java version:
```bash
java -version   # should report 21.x
```

---

## Option A — Docker Compose (recommended)

The simplest path — provisions PostgreSQL and the app together.

```bash
git clone <your-fork-url> quizapp
cd quizapp
docker compose up --build
```

Wait for `Started QuizApplication` in the logs, then open **http://localhost:8080**.

To stop:
```bash
docker compose down          # stop containers, keep data
docker compose down -v       # stop containers AND wipe the database volume
```

---

## Option B — Local Maven + local PostgreSQL

1. **Install PostgreSQL** (if you don't have it) and create the database:
   ```sql
   CREATE DATABASE quizapp;
   ```

2. **Set environment variables.** Either export them in your shell, or create
   `application-local.properties` (already gitignored) alongside the existing profile files and
   run with `-Dspring-boot.run.profiles=local`.

   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/quizapp
   export DB_USERNAME=postgres
   export DB_PASSWORD=your_local_password
   export JWT_SECRET=$(openssl rand -base64 48)
   ```

3. **Run:**
   ```bash
   ./mvnw spring-boot:run
   ```

   Or from your IDE: run `QuizApplication.main()` with the same environment variables set in the
   run configuration.

4. Open **http://localhost:8080**.

### First-boot behavior

On startup, `DataInitializer` seeds (only if the tables are empty):
- An admin account (`ADMIN_EMAIL` / `ADMIN_PASSWORD`, defaulting to `admin@quizapp.com` /
  `Admin@123`)
- A sample quiz ("Java Fundamentals") with 5 questions

Set `SEED_DATA=false` to skip this (e.g. once you have real data you don't want touched).

---

## Option C — In-memory H2 (fastest way to poke around, no Postgres needed)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- Data lives only in memory and resets on every restart.
- H2 web console available at **http://localhost:8080/h2-console**
  (JDBC URL: `jdbc:h2:mem:quizapp`, user `sa`, no password).
- Good for quick manual testing or CI, not for anything you want to keep.

---

## Opening in an IDE

**IntelliJ IDEA**
1. `File → Open` → select the `QuizApplication` folder (the one with `pom.xml`).
2. IntelliJ will detect the Maven project and import dependencies automatically.
3. Set environment variables in the run configuration for `QuizApplication.main()`, or select the
   `dev` profile under *Active profiles* to use H2 with zero config.
4. Run.

**VS Code**
1. Install the "Extension Pack for Java" and "Spring Boot Extension Pack".
2. Open the folder — VS Code will detect the Maven project.
3. Set environment variables in `.vscode/launch.json` or your shell before running.
4. Run `QuizApplication.java` via the Run/Debug panel.

---

## Verifying it's working

1. Open `http://localhost:8080` — you should see the landing page.
2. Log in with the seeded admin (`admin@quizapp.com` / `Admin@123`) → you land on
   `/admin/dashboard`.
3. Register a new student account → you land on `/dashboard` and see "Java Fundamentals" listed.
4. Take the quiz, let the timer run or answer and submit → you should land on `/result/{id}`
   with a full breakdown.
5. Check `/leaderboard` — your attempt should appear.
6. Swagger UI for the REST API: `http://localhost:8080/swagger-ui.html`.

If any of these fail, check the console logs for a stack trace — most first-run issues are
database connectivity (wrong `DB_URL`/credentials) or a port already in use.
