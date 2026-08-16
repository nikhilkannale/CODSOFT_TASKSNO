# Deployment Guide — Render (backend) + Neon (PostgreSQL)

This walks through deploying the Quiz Application so it's reachable at a public URL, using
**Neon** for a managed PostgreSQL database and **Render** for the Spring Boot backend
(Thymeleaf pages are served by the same Spring Boot app — there's no separate frontend deploy).

> These are the exact steps to follow in the Render and Neon dashboards. Since deploying requires
> your own accounts and credentials, this is something you'll need to click through yourself —
> the app is already configured (via `application-prod.properties` and environment variables) to
> make this a config-only exercise, no code changes required.

---

## 1. Provision the database on Neon

1. Sign up / log in at [neon.tech](https://neon.tech).
2. Create a new project (choose a region close to where you'll deploy Render — e.g. both in
   `us-east`).
3. Neon creates a default database and gives you a connection string that looks like:
   ```
   postgresql://<user>:<password>@<host>/<dbname>?sslmode=require
   ```
4. From that, derive the three values Spring needs:
   - `DB_URL` = `jdbc:postgresql://<host>/<dbname>?sslmode=require`
   - `DB_USERNAME` = `<user>`
   - `DB_PASSWORD` = `<password>`
5. Keep this tab open — you'll paste these into Render's environment variables next.

---

## 2. Push your code to GitHub

Render deploys from a Git repository.

```bash
git init
git add .
git commit -m "Initial commit: Quiz Application"
git branch -M main
git remote add origin https://github.com/<your-username>/quizapp.git
git push -u origin main
```

---

## 3. Create the Web Service on Render

1. Sign up / log in at [render.com](https://render.com).
2. **New → Web Service** → connect your GitHub account → select the `quizapp` repo.
3. Configure:
   - **Environment:** `Docker` (Render will build the included `Dockerfile`)
   - **Region:** same region as your Neon project if possible
   - **Instance type:** the free tier works for demo purposes
4. **Environment variables** (Render dashboard → your service → *Environment*):

   | Key | Value |
   |---|---|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `DB_URL` | `jdbc:postgresql://<neon-host>/<dbname>?sslmode=require` |
   | `DB_USERNAME` | from Neon |
   | `DB_PASSWORD` | from Neon |
   | `JWT_SECRET` | a long random value — generate with `openssl rand -base64 48` |
   | `SEED_DATA` | `true` for first deploy (creates the admin account), then switch to `false` |
   | `ADMIN_EMAIL` | your real admin email |
   | `ADMIN_PASSWORD` | a real password, not the sample one |

5. Click **Create Web Service**. Render will build the Docker image and deploy it — the first
   build takes a few minutes.

6. Once deployed, Render gives you a public URL like `https://quizapp-xxxx.onrender.com`. Open
   it — you should see the landing page.

---

## 4. Verify the deployment

1. Log in with the admin account you set via `ADMIN_EMAIL` / `ADMIN_PASSWORD`.
2. Create a quiz, add a few questions.
3. Register a second (student) account in an incognito window and take the quiz.
4. Confirm the result page and leaderboard show real data.
5. **Turn off seeding** once you're happy: set `SEED_DATA=false` in Render's environment
   variables and redeploy, so restarts don't attempt to reseed.

---

## 5. Custom domain (optional)

Render → your service → *Settings* → *Custom Domains* → add your domain and follow the DNS
instructions (a `CNAME` record pointing at the Render-provided hostname).

---

## Notes on this setup

- `spring.jpa.hibernate.ddl-auto=update` means the schema is created/updated automatically on
  boot from the JPA entities — no manual migration step is needed for this project's scope. For
  a larger production system you'd typically switch to a migration tool (Flyway/Liquibase) and
  set `ddl-auto=validate`.
- Render's free tier spins down after inactivity; the first request after idling will be slow
  (cold start) — normal for the free tier, not a bug.
- Because JWTs are stateless, you can scale the Render service to multiple instances without any
  sticky-session configuration — any instance can validate any token signed with the same
  `JWT_SECRET`.
