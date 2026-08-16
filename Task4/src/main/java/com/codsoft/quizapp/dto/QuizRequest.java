package com.codsoft.quizapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizRequest {
    @NotBlank
    private String title;

    private String description;

    @Min(10)
    private Integer durationInSeconds;
}
