package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.dto.QuizSubmission;
import com.codsoft.quizapp.dto.ResultResponse;
import com.codsoft.quizapp.security.UserPrincipal;
import com.codsoft.quizapp.service.ResultService;
import com.codsoft.quizapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@Tag(name = "Results", description = "Submit quiz attempts and review past results")
public class ResultController {

    private final ResultService resultService;
    private final UserService userService;

    @PostMapping("/submit")
    public ResponseEntity<ResultResponse> submit(@AuthenticationPrincipal UserPrincipal principal,
                                                    @Valid @RequestBody QuizSubmission submission) {
        var user = userService.getUserOrThrow(principal.getId());
        return ResponseEntity.ok(resultService.submitQuiz(user, submission));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ResultResponse>> myResults(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(resultService.getResultsForUser(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getResult(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long id) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(resultService.getResultOrThrow(id, principal.getId(), isAdmin));
    }
}
