package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.entity.Result;
import com.codsoft.quizapp.entity.User;
import com.codsoft.quizapp.service.ResultService;
import com.codsoft.quizapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Users & Results", description = "View users and quiz results (ADMIN only)")
public class AdminUserController {

    private final UserService userService;
    private final ResultService resultService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Void> setStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        userService.setEnabled(id, Boolean.TRUE.equals(body.get("enabled")));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/results")
    public ResponseEntity<List<Result>> getAllResults() {
        return ResponseEntity.ok(resultService.getAllResults());
    }

    @GetMapping("/results/quiz/{quizId}")
    public ResponseEntity<List<Result>> getResultsForQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getResultsForQuiz(quizId));
    }
}
