package com.codsoft.quizapp.dto;

import lombok.Data;

/** A single answer for one question, submitted as part of a QuizSubmission. */
@Data
public class AnswerSubmission {
    private Long questionId;
    /** "A", "B", "C", "D" or null/blank if unattempted. */
    private String selectedOption;
}
