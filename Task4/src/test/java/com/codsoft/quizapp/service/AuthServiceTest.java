package com.codsoft.quizapp.service;

import com.codsoft.quizapp.dto.AuthResponse;
import com.codsoft.quizapp.dto.RegisterRequest;
import com.codsoft.quizapp.entity.Role;
import com.codsoft.quizapp.entity.User;
import com.codsoft.quizapp.exception.DuplicateResourceException;
import com.codsoft.quizapp.repository.UserRepository;
import com.codsoft.quizapp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesStudentAccountAndReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Ada Lovelace");
        request.setEmail("Ada@Example.com"); // mixed case -- should be normalized
        request.setPassword("secret123");

        when(userRepository.existsByEmail("ada@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(42L);
            return u;
        });
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("ada@example.com");
        assertThat(response.getRole()).isEqualTo(Role.STUDENT.name());
        assertThat(response.getUserId()).isEqualTo(42L);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Ada Lovelace");
        request.setEmail("ada@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
