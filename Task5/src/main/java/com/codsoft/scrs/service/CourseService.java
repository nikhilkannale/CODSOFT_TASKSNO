package com.codsoft.scrs.service;

import com.codsoft.scrs.dto.CourseRequest;
import com.codsoft.scrs.entity.Course;
import com.codsoft.scrs.exception.DuplicateResourceException;
import com.codsoft.scrs.exception.ResourceNotFoundException;
import com.codsoft.scrs.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    /**
     * Basic in-memory search/filter used by both the REST API and the
     * Thymeleaf course-browsing page (dataset sizes for this kind of
     * academic catalogue are small enough that this stays fast and simple).
     */
    public List<Course> search(String keyword, String department, Integer semester) {
        return courseRepository.findAll().stream()
                .filter(c -> !StringUtils.hasText(keyword)
                        || c.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || c.getCourseCode().toLowerCase().contains(keyword.toLowerCase())
                        || (c.getInstructor() != null && c.getInstructor().toLowerCase().contains(keyword.toLowerCase())))
                .filter(c -> !StringUtils.hasText(department) || department.equalsIgnoreCase(c.getDepartment()))
                .filter(c -> semester == null || semester.equals(c.getSemester()))
                .toList();
    }

    public Course findById(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Course not found with id: null");
        }
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    @Transactional
    @SuppressWarnings("null")
    public Course create(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("A course with code '" + request.getCourseCode() + "' already exists.");
        }
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .department(request.getDepartment())
                .semester(request.getSemester())
                .capacity(request.getCapacity())
                .availableSeats(request.getCapacity())
                .schedule(request.getSchedule())
                .build();
        return courseRepository.save(course);
    }

    @Transactional
    @SuppressWarnings("null")
    public Course update(Long id, CourseRequest request) {
        Course course = findById(id);

        if (!course.getCourseCode().equalsIgnoreCase(request.getCourseCode())
                && courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("A course with code '" + request.getCourseCode() + "' already exists.");
        }

        int seatsTaken = course.getCapacity() - course.getAvailableSeats();
        int newCapacity = request.getCapacity();
        if (newCapacity < seatsTaken) {
            throw new IllegalArgumentException(
                    "Cannot set capacity below the number of students already enrolled (" + seatsTaken + ").");
        }

        course.setCourseCode(request.getCourseCode());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructor(request.getInstructor());
        course.setDepartment(request.getDepartment());
        course.setSemester(request.getSemester());
        course.setCapacity(newCapacity);
        course.setAvailableSeats(newCapacity - seatsTaken);
        course.setSchedule(request.getSchedule());

        return courseRepository.save(course);
    }

    /** Admin action: directly adjust the available seat count (e.g. add extra capacity). */
    @Transactional
    @SuppressWarnings("null")
    public Course adjustCapacity(Long id, int delta) {
        Course course = findById(id);
        int newCapacity = course.getCapacity() + delta;
        int newAvailable = course.getAvailableSeats() + delta;
        if (newCapacity < 0 || newAvailable < 0) {
            throw new IllegalArgumentException("Capacity cannot be reduced below zero or below current enrollment.");
        }
        course.setCapacity(newCapacity);
        course.setAvailableSeats(newAvailable);
        return courseRepository.save(course);
    }

    @Transactional
    @SuppressWarnings("null")
    public void delete(Long id) {
        Course course = findById(id);
        courseRepository.delete(course);
    }

    public long countAll() {
        return courseRepository.count();
    }

    public long totalAvailableSeats() {
        Long sum = courseRepository.sumAvailableSeats();
        return sum == null ? 0 : sum;
    }
}
