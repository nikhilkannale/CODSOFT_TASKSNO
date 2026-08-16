-- =====================================================================
-- Reference seed data for the Quiz Application.
--
-- NOTE: This file is NOT executed automatically (spring.sql.init.mode=never
-- in application.properties). The application seeds the same data
-- programmatically on first boot via DataInitializer.java, which is safer
-- because it BCrypt-hashes the admin password correctly and only seeds when
-- the tables are empty.
--
-- This file is kept for reference / manual import into an empty database,
-- e.g. `psql -d quizapp -f data.sql`. If you use it, generate a real BCrypt
-- hash for the admin password first (the value below is illustrative only).
-- =====================================================================

-- Admin user (password hash below is NOT valid -- replace with a real BCrypt hash)
-- INSERT INTO users (full_name, email, password, role, enabled, created_at)
-- VALUES ('System Admin', 'admin@quizapp.com', '$2a$10$REPLACE_WITH_REAL_BCRYPT_HASH', 'ADMIN', true, now());

-- Sample quiz
INSERT INTO quizzes (title, description, duration_in_seconds, active, created_at)
VALUES ('Java Fundamentals', 'A quick quiz covering Java basics: syntax, OOP, and collections.', 300, true, now());

-- Sample questions (assumes the quiz above got id = 1 in a fresh database)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks) VALUES
  (1, 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'B', 1),
  (1, 'Which collection does not allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'Vector', 'C', 1),
  (1, 'What is the default value of a boolean instance variable?', 'true', 'false', '0', 'null', 'B', 1),
  (1, 'Which keyword prevents a class from being subclassed?', 'static', 'private', 'final', 'const', 'C', 1),
  (1, 'Which of these is NOT a checked exception?', 'IOException', 'SQLException', 'NullPointerException', 'ClassNotFoundException', 'C', 1);
