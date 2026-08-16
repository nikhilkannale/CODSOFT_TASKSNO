package com.codsoft.quizapp.service;

import com.codsoft.quizapp.dto.LeaderboardEntry;
import com.codsoft.quizapp.entity.Result;
import com.codsoft.quizapp.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final ResultRepository resultRepository;

    /** Global leaderboard: each student's single best attempt across all quizzes. */
    public List<LeaderboardEntry> getGlobalLeaderboard() {
        List<Result> all = resultRepository.findAllByOrderBySubmittedAtDesc();

        Map<Long, Result> bestPerUser = all.stream()
                .collect(Collectors.toMap(
                        r -> r.getUser().getId(),
                        r -> r,
                        (a, b) -> a.getScore() >= b.getScore() ? a : b));

        return rank(new ArrayList<>(bestPerUser.values()));
    }

    /** Per-quiz leaderboard: every attempt for a given quiz, sorted by score then speed. */
    public List<LeaderboardEntry> getQuizLeaderboard(Long quizId) {
        List<Result> results = resultRepository.findByQuizIdOrderByScoreDescTimeTakenInSecondsAsc(quizId);
        return rank(results);
    }

    private List<LeaderboardEntry> rank(List<Result> results) {
        results.sort(Comparator
                .comparing(Result::getScore, Comparator.reverseOrder())
                .thenComparing(Result::getTimeTakenInSeconds, Comparator.naturalOrder()));

        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;
        for (Result r : results) {
            entries.add(LeaderboardEntry.builder()
                    .rank(rank++)
                    .studentName(r.getUser().getFullName())
                    .score(r.getScore())
                    .totalMarks(r.getTotalMarks())
                    .percentage(r.getPercentage())
                    .timeTakenInSeconds(r.getTimeTakenInSeconds())
                    .build());
        }
        return entries;
    }
}
