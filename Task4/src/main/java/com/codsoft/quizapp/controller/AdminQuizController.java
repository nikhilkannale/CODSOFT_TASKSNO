package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.dto.QuestionRequest;
import com.codsoft.quizapp.dto.QuizRequest;
import com.codsoft.quizapp.entity.Question;
import com.codsoft.quizapp.entity.Quiz;
import com.codsoft.quizapp.service.QuizService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
@Tag(name = "Admin - Quizzes", description = "Create, edit, delete quizzes and their questions (ADMIN only)")
public class AdminQuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.createQuiz(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(id, request));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        quizService.toggleActive(id, Boolean.TRUE.equals(body.get("active")));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionsForAdmin(quizId));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long quizId, @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(quizService.addQuestion(quizId, request));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(quizService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
