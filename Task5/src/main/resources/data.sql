-- Seed an initial admin account (password: Admin@123 -- BCrypt encoded below)
-- Hash generated for raw password: Admin@123
INSERT INTO students (student_id, full_name, email, password, department, semester, role, created_at)
SELECT 'ADM-0001', 'System Administrator', 'admin@scrs.com',
       '$2b$10$b6jty7XjGHHmL88t4hj/Iu1RwIV/wQF05ySX2bm1lMIvHyj73NVYG',
       'Administration', 0, 'ADMIN', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'admin@scrs.com');
