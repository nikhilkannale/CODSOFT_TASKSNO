package com.codsoft.scrs.controller.web;

import com.codsoft.scrs.dto.RegisterRequest;
import com.codsoft.scrs.exception.DuplicateResourceException;
import com.codsoft.scrs.service.AuthService;
import com.codsoft.scrs.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;
    private final AuthService authService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("courseCount", courseService.countAll());
        model.addAttribute("availableSeats", courseService.totalAvailableSeats());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            authService.register(request);
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
        model.addAttribute("successMessage", "Account created successfully! You can now log in.");
        return "login";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
