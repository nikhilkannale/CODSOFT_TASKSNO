package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.dto.QuestionPublicDto;
import com.codsoft.quizapp.entity.Quiz;
import com.codsoft.quizapp.service.QuizService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Browse available quizzes (student-facing)")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<List<Quiz>> getActiveQuizzes() {
        return ResponseEntity.ok(quizService.getActiveQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizOrThrow(id));
    }

    /** Questions without correct answers -- safe to send to the client while the quiz is in progress. */
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuestionPublicDto>> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getPublicQuestions(id));
    }
}
