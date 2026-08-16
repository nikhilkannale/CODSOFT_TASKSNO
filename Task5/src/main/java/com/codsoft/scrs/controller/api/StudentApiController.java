package com.codsoft.scrs.controller.api;

import com.codsoft.scrs.dto.ChangePasswordRequest;
import com.codsoft.scrs.dto.DashboardStatsResponse;
import com.codsoft.scrs.dto.StudentResponse;
import com.codsoft.scrs.dto.UpdateStudentRequest;
import com.codsoft.scrs.entity.Role;
import com.codsoft.scrs.security.UserPrincipal;
import com.codsoft.scrs.service.CourseService;
import com.codsoft.scrs.service.EnrollmentService;
import com.codsoft.scrs.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student profile management and admin roster views")
public class StudentApiController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all students (admin only)")
    public ResponseEntity<List<StudentResponse>> getAll() {
        return ResponseEntity.ok(studentService.findAll().stream().map(StudentResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Get a student's profile (self or admin)")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(StudentResponse.fromEntity(studentService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Update a student's profile (self or admin)")
    public ResponseEntity<StudentResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(StudentResponse.fromEntity(studentService.update(id, request)));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("#id == principal.id")
    @Operation(summary = "Change the authenticated student's own password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        studentService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a student account (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user's profile")
    public ResponseEntity<StudentResponse> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(StudentResponse.fromEntity(principal.getStudent()));
    }

    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggregate dashboard statistics for the admin panel")
    public ResponseEntity<DashboardStatsResponse> dashboardStats() {
        return ResponseEntity.ok(DashboardStatsResponse.builder()
                .totalStudents(studentService.countByRole(Role.STUDENT))
                .totalCourses(courseService.countAll())
                .activeRegistrations(enrollmentService.countActive())
                .availableSeats(courseService.totalAvailableSeats())
                .build());
    }
}
