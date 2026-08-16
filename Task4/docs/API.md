# REST API Reference

Interactive docs (generated from the same code): once the app is running, visit
**`/swagger-ui.html`**. This file is a hand-written companion reference.

**Base URL:** `http://localhost:8080` (local) or your deployed URL.

**Auth:** JWT, either as a `Authorization: Bearer <token>` header (typical for API clients) or
automatically via the `quizapp_token` HttpOnly cookie set by the login/register endpoints
(what the web UI uses). All endpoints below except `/api/auth/**` and `/api/leaderboard/**`
require authentication; `/api/admin/**` additionally requires the `ADMIN` role.

All error responses share this shape:
```json
{
  "timestamp": "2026-08-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Human-readable explanation",
  "path": "/api/quizzes/5/questions",
  "details": ["field-level validation messages, if any"]
}
```

---

## Authentication — `/api/auth`

### `POST /api/auth/register`
Create a student account. Sets the `quizapp_token` cookie on success.

Request:
```json
{ "fullName": "Ada Lovelace", "email": "ada@example.com", "password": "secret123" }
```
Response `200`:
```json
{
  "token": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "userId": 12,
  "fullName": "Ada Lovelace",
  "email": "ada@example.com",
  "role": "STUDENT"
}
```
Errors: `409 Conflict` if the email is already registered; `400 Bad Request` for validation
failures (blank fields, password under 6 characters).

### `POST /api/auth/login`
```json
{ "email": "ada@example.com", "password": "secret123" }
```
Same response shape as register. `401 Unauthorized` on wrong credentials.

### `POST /api/auth/logout`
No body. Clears the auth cookie.

---

## Users — `/api/users`

### `GET /api/users/me`
Returns the current user's profile (used by the frontend to hydrate the nav bar).
```json
{ "id": 12, "fullName": "Ada Lovelace", "email": "ada@example.com", "role": "STUDENT" }
```

---

## Quizzes (student-facing) — `/api/quizzes`

### `GET /api/quizzes`
List all **active** quizzes.
```json
[
  {
    "id": 1,
    "title": "Java Fundamentals",
    "description": "...",
    "durationInSeconds": 300,
    "active": true,
    "createdAt": "2026-08-01T09:00:00",
    "totalMarks": 5,
    "questionCount": 5
  }
]
```

### `GET /api/quizzes/{id}`
Single quiz, same shape as above.

### `GET /api/quizzes/{id}/questions`
Questions **without the correct answer** — safe to show while a quiz is in progress.
```json
[
  {
    "id": 101,
    "questionText": "Which keyword is used to inherit a class in Java?",
    "optionA": "implements", "optionB": "extends", "optionC": "inherits", "optionD": "super",
    "marks": 1
  }
]
```

---

## Results — `/api/results`

### `POST /api/results/submit`
Grades a submission server-side and persists it. The `correctOption` is never trusted from the
client — it's re-checked against the real question data.

Request:
```json
{
  "quizId": 1,
  "timeTakenInSeconds": 187,
  "answers": [
    { "questionId": 101, "selectedOption": "B" },
    { "questionId": 102, "selectedOption": "" }
  ]
}
```
Response `200`:
```json
{
  "resultId": 55,
  "quizId": 1,
  "quizTitle": "Java Fundamentals",
  "totalQuestions": 5,
  "attemptedQuestions": 4,
  "correctAnswers": 3,
  "incorrectAnswers": 1,
  "score": 3,
  "totalMarks": 5,
  "percentage": 60.0,
  "passed": true,
  "timeTakenInSeconds": 187,
  "submittedAt": "2026-08-15T10:35:22"
}
```
`400 Bad Request` if `quizId` is missing, the quiz has no questions, or an answer references a
question that doesn't belong to the quiz.

### `GET /api/results/me`
List of the current user's past results, newest first (same shape as above, as an array).

### `GET /api/results/{id}`
A single result. Students may only fetch their own; admins may fetch any. `400 Bad Request` if a
student requests someone else's result.

---

## Leaderboard — `/api/leaderboard`
No authentication required.

### `GET /api/leaderboard`
Global leaderboard — each student's single best attempt across all quizzes, ranked by score then
speed.
```json
[
  { "rank": 1, "studentName": "Ada Lovelace", "score": 5, "totalMarks": 5, "percentage": 100.0, "timeTakenInSeconds": 142 }
]
```

### `GET /api/leaderboard/quiz/{quizId}`
Leaderboard for one specific quiz (every attempt, not just each student's best).

---

## Admin — Quizzes & Questions — `/api/admin/quizzes` (ADMIN only)

| Method | Path | Description |
|---|---|---|
| GET | `/api/admin/quizzes` | List every quiz (active and inactive), with question counts |
| POST | `/api/admin/quizzes` | Create a quiz — `{ title, description, durationInSeconds }` |
| PUT | `/api/admin/quizzes/{id}` | Update a quiz's title/description/duration |
| PATCH | `/api/admin/quizzes/{id}/active` | Activate/deactivate — `{ "active": true }` |
| DELETE | `/api/admin/quizzes/{id}` | Delete a quiz (cascades to its questions) |
| GET | `/api/admin/quizzes/{quizId}/questions` | List questions **with** correct answers |
| POST | `/api/admin/quizzes/{quizId}/questions` | Add a question |
| PUT | `/api/admin/quizzes/questions/{questionId}` | Edit a question |
| DELETE | `/api/admin/quizzes/questions/{questionId}` | Delete a question |

Question request body:
```json
{
  "questionText": "Which collection does not allow duplicates?",
  "optionA": "ArrayList", "optionB": "LinkedList", "optionC": "HashSet", "optionD": "Vector",
  "correctOption": "C",
  "marks": 1
}
```

---

## Admin — Users & Results — `/api/admin` (ADMIN only)

| Method | Path | Description |
|---|---|---|
| GET | `/api/admin/users` | List every registered user |
| PATCH | `/api/admin/users/{id}/status` | Enable/disable an account — `{ "enabled": false }` |
| GET | `/api/admin/results` | List every submitted result, across all students and quizzes |
| GET | `/api/admin/results/quiz/{quizId}` | Results for one specific quiz |

---

## HTTP status codes used throughout

| Code | Meaning |
|---|---|
| 200 | Success |
| 204 | Success, no content (deletes) |
| 400 | Validation failure / bad request (details in `details[]` where applicable) |
| 401 | Not authenticated (missing/invalid/expired token) |
| 403 | Authenticated but not authorized (e.g. STUDENT hitting an ADMIN endpoint) |
| 404 | Resource not found |
| 409 | Conflict (duplicate email on registration) |
| 500 | Unexpected server error |
