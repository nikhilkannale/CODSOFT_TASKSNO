package com.codsoft.quizapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the CodSoft Task 4 Quiz Application.
 *
 * A full-stack Spring Boot + Thymeleaf quiz platform with JWT + session based
 * authentication, role based access (ADMIN / STUDENT), timed quizzes,
 * automatic submission, results and a leaderboard.
 */
@SpringBootApplication
public class QuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
    }
}
