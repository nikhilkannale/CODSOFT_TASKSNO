package com.codsoft.scrs.dto;

import com.codsoft.scrs.entity.Role;
import com.codsoft.scrs.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String studentId;
    private String fullName;
    private String email;
    private String department;
    private Integer semester;
    private String profilePictureUrl;
    private Role role;
    private LocalDateTime createdAt;

    public static StudentResponse fromEntity(Student s) {
        return StudentResponse.builder()
                .id(s.getId())
                .studentId(s.getStudentId())
                .fullName(s.getFullName())
                .email(s.getEmail())
                .department(s.getDepartment())
                .semester(s.getSemester())
                .profilePictureUrl(s.getProfilePictureUrl())
                .role(s.getRole())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
