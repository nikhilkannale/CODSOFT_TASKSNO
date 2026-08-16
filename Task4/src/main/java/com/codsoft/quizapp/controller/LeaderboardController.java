package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.dto.LeaderboardEntry;
import com.codsoft.quizapp.service.LeaderboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Public leaderboards")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> global() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<LeaderboardEntry>> forQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(leaderboardService.getQuizLeaderboard(quizId));
    }
}
