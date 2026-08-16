package com.codsoft.scrs.controller.api;

import com.codsoft.scrs.dto.EnrollmentRequest;
import com.codsoft.scrs.dto.EnrollmentResponse;
import com.codsoft.scrs.entity.Enrollment;
import com.codsoft.scrs.security.UserPrincipal;
import com.codsoft.scrs.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Course registration and drop endpoints")
public class EnrollmentApiController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Register the current student (or, for admins, a specified student) into a course")
    public ResponseEntity<EnrollmentResponse> register(@Valid @RequestBody EnrollmentRequest request,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getStudent().getRole().name().equals("ADMIN");
        Long targetStudentId = (isAdmin && request.getStudentId() != null) ? request.getStudentId() : principal.getId();

        Enrollment enrollment = enrollmentService.register(targetStudentId, request.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnrollmentResponse.fromEntity(enrollment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Drop a course registration (owner or admin)")
    public ResponseEntity<Void> drop(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getStudent().getRole().name().equals("ADMIN");
        enrollmentService.drop(id, principal.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or #studentId == principal.id")
    @Operation(summary = "List a student's course registrations (owner or admin)")
    public ResponseEntity<List<EnrollmentResponse>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.findByStudent(studentId).stream()
                .map(EnrollmentResponse::fromEntity).toList());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all course registrations (admin only)")
    public ResponseEntity<List<EnrollmentResponse>> all() {
        return ResponseEntity.ok(enrollmentService.findAll().stream()
                .map(EnrollmentResponse::fromEntity).toList());
    }
}
