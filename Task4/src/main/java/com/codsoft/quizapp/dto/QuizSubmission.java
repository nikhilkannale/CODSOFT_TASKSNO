package com.codsoft.quizapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizSubmission {
    private Long quizId;
    private List<AnswerSubmission> answers;
    /** Seconds elapsed between quiz start and submission, tracked client-side and validated server-side. */
    private Integer timeTakenInSeconds;
}
