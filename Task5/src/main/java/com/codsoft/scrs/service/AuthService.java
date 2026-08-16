package com.codsoft.scrs.service;

import com.codsoft.scrs.dto.AuthResponse;
import com.codsoft.scrs.dto.LoginRequest;
import com.codsoft.scrs.dto.RegisterRequest;
import com.codsoft.scrs.entity.Role;
import com.codsoft.scrs.entity.Student;
import com.codsoft.scrs.exception.DuplicateResourceException;
import com.codsoft.scrs.exception.InvalidCredentialsException;
import com.codsoft.scrs.repository.StudentRepository;
import com.codsoft.scrs.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    @SuppressWarnings("null")
    public AuthResponse register(RegisterRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        String generatedStudentId = "STU-" + System.currentTimeMillis() % 1_000_000;

        Student student = Student.builder()
                .studentId(generatedStudentId)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(request.getDepartment())
                .semester(request.getSemester())
                .role(Role.STUDENT)
                .build();

        student = studentRepository.save(student);
        String token = jwtUtil.generateToken(student.getEmail(), student.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .studentId(student.getId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .role(student.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(student.getEmail(), student.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .studentId(student.getId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .role(student.getRole().name())
                .build();
    }
}
