package com.codsoft.scrs.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateStudentRequest {
    private String fullName;

    @Email(message = "Email must be valid")
    private String email;

    private String department;

    private Integer semester;
}
