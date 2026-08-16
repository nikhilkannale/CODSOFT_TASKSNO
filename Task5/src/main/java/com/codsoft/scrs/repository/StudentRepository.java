package com.codsoft.scrs.repository;

import com.codsoft.scrs.entity.Role;
import com.codsoft.scrs.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByStudentId(String studentId);
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    long countByRole(Role role);
}
