package com.codsoft.scrs.controller.web;

import com.codsoft.scrs.dto.ChangePasswordRequest;
import com.codsoft.scrs.dto.UpdateStudentRequest;
import com.codsoft.scrs.entity.Course;
import com.codsoft.scrs.entity.Enrollment;
import com.codsoft.scrs.entity.Student;
import com.codsoft.scrs.exception.AlreadyEnrolledException;
import com.codsoft.scrs.exception.CourseCapacityFullException;
import com.codsoft.scrs.exception.InvalidCredentialsException;
import com.codsoft.scrs.security.UserPrincipal;
import com.codsoft.scrs.service.CourseService;
import com.codsoft.scrs.service.EnrollmentService;
import com.codsoft.scrs.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentWebController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Student student = principal.getStudent();
        List<Enrollment> active = enrollmentService.findActiveByStudent(student.getId());
        model.addAttribute("student", student);
        model.addAttribute("activeCount", active.size());
        model.addAttribute("enrollments", active);
        model.addAttribute("availableCourses", courseService.countAll());
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String department,
                           @RequestParam(required = false) Integer semester,
                           Model model) {
        List<Course> courses = (keyword != null || department != null || semester != null)
                ? courseService.search(keyword, department, semester)
                : courseService.findAll();
        model.addAttribute("courses", courses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);
        model.addAttribute("semester", semester);
        return "student/courses";
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal, Model model) {
        Course course = courseService.findById(id);
        boolean alreadyEnrolled = enrollmentService.findActiveByStudent(principal.getId()).stream()
                .anyMatch(e -> e.getCourse().getId().equals(id));
        model.addAttribute("course", course);
        model.addAttribute("alreadyEnrolled", alreadyEnrolled);
        return "student/course-details";
    }

    @PostMapping("/courses/{id}/register")
    public String register(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.register(principal.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "You have successfully registered for the course!");
        } catch (AlreadyEnrolledException | CourseCapacityFullException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/courses/" + id;
    }

    @PostMapping("/enrollments/{enrollmentId}/drop")
    public String drop(@PathVariable Long enrollmentId, @AuthenticationPrincipal UserPrincipal principal,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.drop(enrollmentId, principal.getId(), false);
            redirectAttributes.addFlashAttribute("successMessage", "Course dropped successfully. The seat has been released.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/my-courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<Enrollment> all = enrollmentService.findByStudent(principal.getId());
        model.addAttribute("enrollments", all);
        return "student/my-courses";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Student student = studentService.findById(principal.getId());
        UpdateStudentRequest form = new UpdateStudentRequest();
        form.setFullName(student.getFullName());
        form.setEmail(student.getEmail());
        form.setDepartment(student.getDepartment());
        form.setSemester(student.getSemester());
        model.addAttribute("student", student);
        model.addAttribute("updateRequest", form);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("updateRequest") UpdateStudentRequest request,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserPrincipal principal,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please correct the highlighted fields.");
            return "redirect:/student/profile";
        }
        studentService.update(principal.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                  @AuthenticationPrincipal UserPrincipal principal,
                                  org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            studentService.changePassword(principal.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        } catch (InvalidCredentialsException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/picture")
    public String uploadPicture(@RequestParam("file") MultipartFile file,
                                 @AuthenticationPrincipal UserPrincipal principal,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose an image to upload.");
            return "redirect:/student/profile";
        }
        try {
            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            file.transferTo(target);
            studentService.updateProfilePicture(principal.getId(), "/uploads/" + filename);
            redirectAttributes.addFlashAttribute("successMessage", "Profile picture updated.");
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image. Please try again.");
        }
        return "redirect:/student/profile";
    }
}
