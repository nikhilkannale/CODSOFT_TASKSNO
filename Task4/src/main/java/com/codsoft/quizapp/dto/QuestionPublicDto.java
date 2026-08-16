package com.codsoft.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Question shape sent to students while a quiz is in progress -- never exposes the correct answer. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionPublicDto {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer marks;
}
