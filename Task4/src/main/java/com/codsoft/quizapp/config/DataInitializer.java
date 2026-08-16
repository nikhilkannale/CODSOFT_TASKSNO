package com.codsoft.quizapp.config;

import com.codsoft.quizapp.entity.*;
import com.codsoft.quizapp.repository.QuizRepository;
import com.codsoft.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds a default admin account and a sample quiz on first boot so the
 * application is immediately usable/demoable. Controlled by app.seed-data
 * (enabled by default; disable in production once real data exists).
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-data:true}")
    private boolean seedData;

    @Value("${app.admin.email:admin@quizapp.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!seedData) return;

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .fullName("System Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }

        if (quizRepository.count() == 0) {
            Quiz quiz = Quiz.builder()
                    .title("Java Fundamentals")
                    .description("A quick quiz covering Java basics: syntax, OOP, and collections.")
                    .durationInSeconds(300)
                    .active(true)
                    .build();

            quiz.setQuestions(List.of(
                    Question.builder().quiz(quiz)
                            .questionText("Which keyword is used to inherit a class in Java?")
                            .optionA("implements").optionB("extends").optionC("inherits").optionD("super")
                            .correctOption("B").marks(1).build(),
                    Question.builder().quiz(quiz)
                            .questionText("Which collection does not allow duplicate elements?")
                            .optionA("ArrayList").optionB("LinkedList").optionC("HashSet").optionD("Vector")
                            .correctOption("C").marks(1).build(),
                    Question.builder().quiz(quiz)
                            .questionText("What is the default value of a boolean instance variable?")
                            .optionA("true").optionB("false").optionC("0").optionD("null")
                            .correctOption("B").marks(1).build(),
                    Question.builder().quiz(quiz)
                            .questionText("Which keyword prevents a class from being subclassed?")
                            .optionA("static").optionB("private").optionC("final").optionD("const")
                            .correctOption("C").marks(1).build(),
                    Question.builder().quiz(quiz)
                            .questionText("Which of these is NOT a checked exception?")
                            .optionA("IOException").optionB("SQLException").optionC("NullPointerException").optionD("ClassNotFoundException")
                            .correctOption("C").marks(1).build()
            ));

            quizRepository.save(quiz);
        }
    }
}
