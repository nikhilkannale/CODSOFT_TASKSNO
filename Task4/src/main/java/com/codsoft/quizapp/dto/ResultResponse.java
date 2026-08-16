package com.codsoft.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultResponse {
    private Long resultId;
    private Long quizId;
    private String quizTitle;
    private Integer totalQuestions;
    private Integer attemptedQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Integer score;
    private Integer totalMarks;
    private Double percentage;
    private Boolean passed;
    private Integer timeTakenInSeconds;
    private LocalDateTime submittedAt;
}
