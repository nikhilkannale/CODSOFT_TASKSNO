package com.codsoft.scrs.controller.api;

import com.codsoft.scrs.dto.CourseRequest;
import com.codsoft.scrs.dto.CourseResponse;
import com.codsoft.scrs.entity.Course;
import com.codsoft.scrs.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Browse, create, update and delete courses")
public class CourseApiController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "List all courses, optionally filtered by keyword/department/semester")
    public ResponseEntity<List<CourseResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer semester) {
        List<Course> courses = (keyword != null || department != null || semester != null)
                ? courseService.search(keyword, department, semester)
                : courseService.findAll();
        return ResponseEntity.ok(courses.stream().map(CourseResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single course by id")
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(CourseResponse.fromEntity(courseService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new course (admin only)")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.fromEntity(courseService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing course (admin only)")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(CourseResponse.fromEntity(courseService.update(id, request)));
    }

    @PatchMapping("/{id}/capacity")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Increase (positive) or decrease (negative) a course's capacity (admin only)")
    public ResponseEntity<CourseResponse> adjustCapacity(@PathVariable Long id, @RequestParam int delta) {
        return ResponseEntity.ok(CourseResponse.fromEntity(courseService.adjustCapacity(id, delta)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a course (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
