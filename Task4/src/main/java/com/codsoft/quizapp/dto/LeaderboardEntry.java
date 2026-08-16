package com.codsoft.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaderboardEntry {
    private Integer rank;
    private String studentName;
    private Integer score;
    private Integer totalMarks;
    private Double percentage;
    private Integer timeTakenInSeconds;
}
