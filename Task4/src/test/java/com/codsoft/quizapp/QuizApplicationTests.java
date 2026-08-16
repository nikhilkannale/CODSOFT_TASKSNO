package com.codsoft.quizapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the full Spring context (security, JPA, MVC, JWT beans)
 * wires up without errors. Uses the "dev" profile so it runs against an
 * in-memory H2 database with no external dependencies.
 */
@SpringBootTest
@ActiveProfiles("dev")
class QuizApplicationTests {

    @Test
    void contextLoads() {
        // If the Spring context fails to start, this test fails.
    }
}
