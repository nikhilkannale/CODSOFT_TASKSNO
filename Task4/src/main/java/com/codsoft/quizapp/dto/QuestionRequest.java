package com.codsoft.quizapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank
    private String questionText;

    @NotBlank
    private String optionA;

    @NotBlank
    private String optionB;

    @NotBlank
    private String optionC;

    @NotBlank
    private String optionD;

    @NotBlank
    @Pattern(regexp = "[ABCDabcd]", message = "correctOption must be one of A, B, C, D")
    private String correctOption;

    @Min(1)
    private Integer marks = 1;
}
