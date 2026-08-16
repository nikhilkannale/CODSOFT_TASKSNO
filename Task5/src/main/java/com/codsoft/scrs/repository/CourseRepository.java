package com.codsoft.scrs.repository;

import com.codsoft.scrs.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    boolean existsByCourseCode(String courseCode);

    @Query("SELECT COALESCE(SUM(c.availableSeats), 0) FROM Course c")
    Long sumAvailableSeats();
}
