package com.codsoft.scrs.dto;

import com.codsoft.scrs.entity.Enrollment;
import com.codsoft.scrs.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;

    public static EnrollmentResponse fromEntity(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .studentName(e.getStudent().getFullName())
                .courseId(e.getCourse().getId())
                .courseCode(e.getCourse().getCourseCode())
                .courseTitle(e.getCourse().getTitle())
                .enrollmentDate(e.getEnrollmentDate())
                .status(e.getStatus())
                .build();
    }
}
