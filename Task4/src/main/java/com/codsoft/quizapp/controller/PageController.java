package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Server-rendered (Thymeleaf) page routes. All pages fetch their actual data
 * client-side from the REST API under /api/** using the shared quizapp_token
 * cookie, keeping a single source of truth for business logic.
 */
@Controller
public class PageController {

    @GetMapping({"/", "/home"})
    public String home(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return "index";
        }
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? "redirect:/admin/dashboard" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/quiz/{id}")
    public String takeQuiz(@PathVariable Long id, Model model) {
        model.addAttribute("quizId", id);
        return "quiz-take";
    }

    @GetMapping("/result/{id}")
    public String result(@PathVariable Long id, Model model) {
        model.addAttribute("resultId", id);
        return "result";
    }

    @GetMapping("/my-results")
    public String myResults() {
        return "my-results";
    }

    @GetMapping("/leaderboard")
    public String leaderboard() {
        return "leaderboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/quizzes")
    public String adminQuizzes() {
        return "admin/quizzes";
    }

    @GetMapping("/admin/quizzes/{id}/questions")
    public String adminQuestions(@PathVariable Long id, Model model) {
        model.addAttribute("quizId", id);
        return "admin/questions";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/results")
    public String adminResults() {
        return "admin/results";
    }

    @GetMapping("/admin/leaderboard")
    public String adminLeaderboard() {
        return "admin/leaderboard";
    }
}
