package com.codsoft.scrs.service;

import com.codsoft.scrs.dto.ChangePasswordRequest;
import com.codsoft.scrs.dto.UpdateStudentRequest;
import com.codsoft.scrs.entity.Role;
import com.codsoft.scrs.entity.Student;
import com.codsoft.scrs.exception.DuplicateResourceException;
import com.codsoft.scrs.exception.InvalidCredentialsException;
import com.codsoft.scrs.exception.ResourceNotFoundException;
import com.codsoft.scrs.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    @Transactional
    public Student update(Long id, UpdateStudentRequest request) {
        Student student = findById(id);

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(student.getEmail())
                && studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            student.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            student.setEmail(request.getEmail());
        }
        if (request.getDepartment() != null) {
            student.setDepartment(request.getDepartment());
        }
        if (request.getSemester() != null) {
            student.setSemester(request.getSemester());
        }
        return studentRepository.save(student);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        Student student = findById(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), student.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }
        student.setPassword(passwordEncoder.encode(request.getNewPassword()));
        studentRepository.save(student);
    }

    @Transactional
    public void updateProfilePicture(Long id, String url) {
        Student student = findById(id);
        student.setProfilePictureUrl(url);
        studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        Student student = findById(id);
        studentRepository.delete(student);
    }

    public long countByRole(Role role) {
        return studentRepository.countByRole(role);
    }
}
