package com.codsoft.quizapp.controller;

import com.codsoft.quizapp.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Current user profile")
public class UserController {

    /**
     * Lets the browser-side JS discover who is logged in (name, email, role)
     * without being able to read the HttpOnly auth cookie directly.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(Map.of(
                "id", principal.getId(),
                "fullName", principal.getFullName(),
                "email", principal.getEmail(),
                "role", isAdmin ? "ADMIN" : "STUDENT"
        ));
    }
}
