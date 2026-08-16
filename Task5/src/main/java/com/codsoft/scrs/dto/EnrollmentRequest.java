package com.codsoft.scrs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {
    @NotNull(message = "Course id is required")
    private Long courseId;

    // Optional: only used by admins enrolling on behalf of a student.
    // A student calling this endpoint always enrolls themselves.
    private Long studentId;
}
