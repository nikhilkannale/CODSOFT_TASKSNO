# Testing Guide

## Automated tests

```bash
./mvnw test
```

What's covered:
- **`QuizApplicationTests`** — full Spring context load (Security + JPA + MVC + JWT beans wire up
  correctly), run against the `dev` profile (in-memory H2, no external dependencies needed).
- **`ResultServiceTest`** — the core grading logic: full-marks scoring, partial credit, blank
  answers, garbage/invalid option letters, rejecting answers for questions outside the quiz, and
  clamping a tampered `timeTakenInSeconds` to the quiz's real duration.
- **`AuthServiceTest`** — registration (including email normalization) and duplicate-email
  rejection.

These intentionally focus on the logic most worth protecting with tests: grading must never trust
the client, and auth must not allow duplicate accounts or leak details on failure.

---

## Manual test checklist

Use this after any change touching auth, quiz-taking, or grading — it exercises the full user
journey end to end.

### Registration & login
- [ ] Register a new student account → redirected to `/dashboard`
- [ ] Log out, log back in with the same credentials → succeeds
- [ ] Try registering the same email twice → clear error message, no duplicate account created
- [ ] Try logging in with a wrong password → clear error message, no account lockout info leaked
- [ ] Visit `/dashboard` while logged out → redirected to `/login`

### Taking a quiz
- [ ] Dashboard lists only **active** quizzes
- [ ] Starting a quiz shows one question at a time with a live countdown
- [ ] Selecting an answer highlights it; moving to the next question keeps the selection if you
      go back
- [ ] Refresh the page mid-quiz → the timer continues from the correct remaining time (not reset
      to the full duration)
- [ ] Let the timer hit zero → the quiz auto-submits and you land on the result page
- [ ] Submit manually with some questions unanswered → confirmation prompt, then submits
- [ ] After submitting, going back to `/quiz/{id}` starts a **fresh** attempt (old localStorage
      state was cleared)

### Results
- [ ] Result page shows correct score, percentage, pass/fail, and time taken
- [ ] `/my-results` lists the attempt, clicking it opens the same result
- [ ] A student cannot view another student's result by guessing the result ID in the URL
      (`GET /api/results/{id}` returns 400 for a result that isn't theirs)

### Leaderboard
- [ ] `/leaderboard` shows the attempt, ranked correctly by score then speed
- [ ] Taking the same quiz again with a higher score updates your position on the **global**
      leaderboard (which keeps your best attempt only)

### Admin
- [ ] Log in as `admin@quizapp.com` / `Admin@123` → redirected to `/admin/dashboard`
- [ ] Create a new quiz, add 2–3 questions with different correct options
- [ ] Edit a question's text/options → changes reflect immediately for new attempts
- [ ] Deactivate a quiz → it disappears from the student dashboard, but past results referencing
      it are unaffected
- [ ] Delete a quiz → its questions are deleted too (cascade); confirm no orphaned rows
- [ ] `/admin/users` lists every registered user; disabling one prevents further login attempts
- [ ] `/admin/results` shows every submitted attempt across all students
- [ ] A logged-in **student** hitting any `/admin/**` URL or `/api/admin/**` endpoint directly
      gets redirected/`403`, not the admin content

### API-level checks (via Swagger UI or curl)
- [ ] `GET /api/quizzes/{id}/questions` never includes `correctOption` in the response
- [ ] `POST /api/results/submit` with a `selectedOption` of `"Z"` (invalid) is treated as
      unattempted, not as an error
- [ ] `POST /api/results/submit` with a `questionId` belonging to a different quiz returns `400`
- [ ] All `/api/admin/**` endpoints return `401` when unauthenticated and `403` for a
      `STUDENT`-role token
