package com.codsoft.scrs.controller.web;

import com.codsoft.scrs.dto.CourseRequest;
import com.codsoft.scrs.entity.Role;
import com.codsoft.scrs.exception.DuplicateResourceException;
import com.codsoft.scrs.service.CourseService;
import com.codsoft.scrs.service.EnrollmentService;
import com.codsoft.scrs.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentService.countByRole(Role.STUDENT));
        model.addAttribute("totalCourses", courseService.countAll());
        model.addAttribute("activeRegistrations", enrollmentService.countActive());
        model.addAttribute("availableSeats", courseService.totalAvailableSeats());
        return "admin/dashboard";
    }

    // ---------- Courses ----------

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "admin/courses";
    }

    @GetMapping("/courses/add")
    public String addCourseForm(Model model) {
        if (!model.containsAttribute("courseRequest")) {
            model.addAttribute("courseRequest", new CourseRequest());
        }
        return "admin/add-course";
    }

    @PostMapping("/courses/add")
    public String addCourseSubmit(@Valid @ModelAttribute("courseRequest") CourseRequest request,
                                   BindingResult bindingResult, Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/add-course";
        }
        try {
            courseService.create(request);
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/add-course";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Course '" + request.getTitle() + "' added successfully.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model) {
        var course = courseService.findById(id);
        CourseRequest form = new CourseRequest();
        form.setCourseCode(course.getCourseCode());
        form.setTitle(course.getTitle());
        form.setDescription(course.getDescription());
        form.setInstructor(course.getInstructor());
        form.setDepartment(course.getDepartment());
        form.setSemester(course.getSemester());
        form.setCapacity(course.getCapacity());
        form.setSchedule(course.getSchedule());
        model.addAttribute("courseId", id);
        model.addAttribute("courseRequest", form);
        return "admin/edit-course";
    }

    @PostMapping("/courses/{id}/edit")
    public String editCourseSubmit(@PathVariable Long id, @Valid @ModelAttribute("courseRequest") CourseRequest request,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courseId", id);
            return "admin/edit-course";
        }
        try {
            courseService.update(id, request);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("courseId", id);
            return "admin/edit-course";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{id}/capacity")
    public String adjustCapacity(@PathVariable Long id, @RequestParam int delta, RedirectAttributes redirectAttributes) {
        try {
            courseService.adjustCapacity(id, delta);
            redirectAttributes.addFlashAttribute("successMessage", "Course capacity updated.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // ---------- Students ----------

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.findAll().stream()
                .filter(s -> s.getRole() == Role.STUDENT).toList());
        return "admin/students";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student removed successfully.");
        return "redirect:/admin/students";
    }

    // ---------- Registrations ----------

    @GetMapping("/registrations")
    public String registrations(Model model) {
        model.addAttribute("enrollments", enrollmentService.findAll());
        return "admin/registrations";
    }

    @PostMapping("/registrations/{id}/drop")
    public String dropRegistration(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.drop(id, null, true);
            redirectAttributes.addFlashAttribute("successMessage", "Registration dropped and seat released.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/registrations";
    }
}
