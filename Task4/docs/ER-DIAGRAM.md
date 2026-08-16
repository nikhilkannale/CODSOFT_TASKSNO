# Database Schema & ER Diagram

## Entity-relationship diagram

```
┌────────────────────┐          ┌────────────────────────┐
│        users        │          │         quizzes         │
├────────────────────┤          ├────────────────────────┤
│ id            PK    │          │ id                 PK   │
│ full_name           │          │ title                   │
│ email        UNIQUE │          │ description              │
│ password (bcrypt)   │          │ duration_in_seconds      │
│ role  (ADMIN/       │          │ active                   │
│        STUDENT)     │          │ created_at               │
│ enabled              │          └───────────┬──────────────┘
│ created_at          │                      │ 1
└─────────┬───────────┘                      │
          │ 1                                │ *
          │                     ┌─────────────▼──────────────┐
          │                     │          questions           │
          │                     ├─────────────────────────────┤
          │                     │ id                    PK     │
          │                     │ quiz_id               FK     │
          │                     │ question_text                │
          │                     │ option_a / b / c / d          │
          │                     │ correct_option (A–D)          │
          │                     │ marks                         │
          │                     └───────────────────────────────┘
          │ *
┌─────────▼────────────────────────────────────────────────────┐
│                            results                              │
├──────────────────────────────────────────────────────────────┤
│ id                       PK                                    │
│ user_id                  FK → users.id                          │
│ quiz_id                  FK → quizzes.id                         │
│ total_questions                                                  │
│ attempted_questions                                              │
│ correct_answers                                                  │
│ incorrect_answers                                                │
│ score                                                            │
│ total_marks                                                      │
│ percentage                                                       │
│ passed                                                           │
│ time_taken_in_seconds                                            │
│ submitted_at                                                     │
└──────────────────────────────────────────────────────────────┘
```

**Relationships**
- `User (1) —— (*) Result` — a student can have many results, one per quiz attempt
- `Quiz (1) —— (*) Question` — a quiz has many questions (cascade delete: deleting a quiz
  deletes its questions)
- `Quiz (1) —— (*) Result` — a quiz can be attempted by many students (and re-attempted, since
  there's no uniqueness constraint on `(user_id, quiz_id)` — each submission creates a new row)

---

## Table definitions

### `users`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, auto-increment |
| full_name | VARCHAR | NOT NULL |
| email | VARCHAR | NOT NULL, UNIQUE |
| password | VARCHAR | NOT NULL — BCrypt hash, never returned in API responses |
| role | VARCHAR | NOT NULL — `ADMIN` or `STUDENT` |
| enabled | BOOLEAN | NOT NULL, default `true` |
| created_at | TIMESTAMP | |

### `quizzes`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, auto-increment |
| title | VARCHAR | NOT NULL |
| description | VARCHAR(1000) | |
| duration_in_seconds | INTEGER | NOT NULL, min 10 |
| active | BOOLEAN | NOT NULL, default `true` — inactive quizzes are hidden from students |
| created_at | TIMESTAMP | |

### `questions`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, auto-increment |
| quiz_id | BIGINT | FK → quizzes.id, NOT NULL |
| question_text | VARCHAR(1000) | NOT NULL |
| option_a..d | VARCHAR | NOT NULL each |
| correct_option | VARCHAR(1) | NOT NULL — one of `A`/`B`/`C`/`D` |
| marks | INTEGER | NOT NULL, min 1, default 1 |

### `results`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, auto-increment |
| user_id | BIGINT | FK → users.id, NOT NULL |
| quiz_id | BIGINT | FK → quizzes.id, NOT NULL |
| total_questions | INTEGER | NOT NULL |
| attempted_questions | INTEGER | NOT NULL |
| correct_answers | INTEGER | NOT NULL |
| incorrect_answers | INTEGER | NOT NULL |
| score | INTEGER | NOT NULL — sum of marks for correct answers |
| total_marks | INTEGER | NOT NULL — sum of marks across all questions in the quiz |
| percentage | DOUBLE | NOT NULL — `score / total_marks * 100`, rounded to 2 decimals |
| passed | BOOLEAN | NOT NULL — `percentage >= 40.0` |
| time_taken_in_seconds | INTEGER | NOT NULL — clamped to `[0, quiz.duration_in_seconds]` |
| submitted_at | TIMESTAMP | |

---

## Notes

- Schema is created/kept in sync automatically via Hibernate (`ddl-auto=update`) from the JPA
  entity definitions in `src/main/java/com/codsoft/quizapp/entity/`. There is no separate SQL
  migration to keep manually in sync with the code.
- The pass threshold (40%) is defined as a constant (`ResultService.PASS_PERCENTAGE`) rather than
  a database column — it's an application-level policy, not per-quiz configurable data, in this
  version of the app. Making it configurable per-quiz would just mean adding a
  `pass_percentage` column to `quizzes` and reading it in `ResultService`.
