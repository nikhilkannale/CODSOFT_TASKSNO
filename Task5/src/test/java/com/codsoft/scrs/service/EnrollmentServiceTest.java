package com.codsoft.scrs.service;

import com.codsoft.scrs.entity.*;
import com.codsoft.scrs.exception.AlreadyEnrolledException;
import com.codsoft.scrs.exception.CourseCapacityFullException;
import com.codsoft.scrs.repository.CourseRepository;
import com.codsoft.scrs.repository.EnrollmentRepository;
import com.codsoft.scrs.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises the core registration business rules end-to-end against an
 * in-memory H2 database: capacity enforcement, duplicate-registration
 * prevention, and seat restoration on drop.
 */
@SpringBootTest
@Transactional
class EnrollmentServiceTest {

    @Autowired private EnrollmentService enrollmentService;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = studentRepository.save(Student.builder()
                .studentId("STU-TEST-1").fullName("Test Student").email("test@student.com")
                .password("hashed").role(Role.STUDENT).build());

        course = courseRepository.save(Course.builder()
                .courseCode("TST101").title("Test Course").capacity(1).availableSeats(1).build());
    }

    @Test
    void registerReducesAvailableSeats() {
        enrollmentService.register(student.getId(), course.getId());
        Course refreshed = courseRepository.findById(course.getId()).orElseThrow();
        assertEquals(0, refreshed.getAvailableSeats());
    }

    @Test
    void registerTwiceThrowsAlreadyEnrolled() {
        enrollmentService.register(student.getId(), course.getId());
        Student other = student; // same student, different course attempt
        assertThrows(AlreadyEnrolledException.class,
                () -> enrollmentService.register(other.getId(), course.getId()));
    }

    @Test
    void registerWhenFullThrowsCapacityException() {
        Student second = studentRepository.save(Student.builder()
                .studentId("STU-TEST-2").fullName("Second Student").email("test2@student.com")
                .password("hashed").role(Role.STUDENT).build());

        enrollmentService.register(student.getId(), course.getId()); // fills the single seat
        assertThrows(CourseCapacityFullException.class,
                () -> enrollmentService.register(second.getId(), course.getId()));
    }

    @Test
    void dropRestoresAvailableSeat() {
        var enrollment = enrollmentService.register(student.getId(), course.getId());
        enrollmentService.drop(enrollment.getId(), student.getId(), false);
        Course refreshed = courseRepository.findById(course.getId()).orElseThrow();
        assertEquals(1, refreshed.getAvailableSeats());
        assertEquals(EnrollmentStatus.DROPPED,
                enrollmentRepository.findById(enrollment.getId()).orElseThrow().getStatus());
    }
}
