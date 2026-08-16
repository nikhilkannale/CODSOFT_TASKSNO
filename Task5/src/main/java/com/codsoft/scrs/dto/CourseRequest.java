package com.codsoft.scrs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course title is required")
    private String title;

    private String description;
    private String instructor;
    private String department;
    private Integer semester;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String schedule;
}
