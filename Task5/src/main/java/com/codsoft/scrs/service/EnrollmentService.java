package com.codsoft.scrs.service;

import com.codsoft.scrs.entity.Course;
import com.codsoft.scrs.entity.Enrollment;
import com.codsoft.scrs.entity.EnrollmentStatus;
import com.codsoft.scrs.entity.Student;
import com.codsoft.scrs.exception.AlreadyEnrolledException;
import com.codsoft.scrs.exception.CourseCapacityFullException;
import com.codsoft.scrs.exception.ResourceNotFoundException;
import com.codsoft.scrs.repository.CourseRepository;
import com.codsoft.scrs.repository.EnrollmentRepository;
import com.codsoft.scrs.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Owns all course-registration business rules:
 *  - a student may not register twice for the same active course
 *  - registration is only permitted while seats remain
 *  - seat counts are atomically decremented/incremented on register/drop
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public @NonNull Enrollment register(@NonNull Long studentId, @NonNull Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Pessimistic-friendly re-fetch inside the transaction to keep the seat check and decrement atomic.
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE)) {
            throw new AlreadyEnrolledException("You are already registered for '" + course.getTitle() + "'.");
        }

        if (course.isFull()) {
            throw new CourseCapacityFullException(
                    "'" + course.getTitle() + "' is at full capacity. No seats are currently available.");
        }

        course.setAvailableSeats(course.getAvailableSeats() - 1);
        courseRepository.save(course);

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void drop(@NonNull Long enrollmentId, Long requestingStudentId, boolean isAdmin) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (!isAdmin && !enrollment.getStudent().getId().equals(requestingStudentId)) {
            throw new org.springframework.security.access.AccessDeniedException("You may only drop your own course registrations.");
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new IllegalArgumentException("This registration is not active and cannot be dropped again.");
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);

        Course course = enrollment.getCourse();
        int restored = Math.min(course.getCapacity(), course.getAvailableSeats() + 1);
        course.setAvailableSeats(restored);
        courseRepository.save(course);
    }

    public List<Enrollment> findByStudent(@NonNull Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> findActiveByStudent(@NonNull Long studentId) {
        return enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    public long countActive() {
        return enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
    }
}
