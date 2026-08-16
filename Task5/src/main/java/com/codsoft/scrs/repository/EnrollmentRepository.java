package com.codsoft.scrs.repository;

import com.codsoft.scrs.entity.Enrollment;
import com.codsoft.scrs.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.status = 'ACTIVE'")
    List<Enrollment> findAllActive();
}
