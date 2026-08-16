package com.codsoft.quizapp.service;

import com.codsoft.quizapp.entity.User;
import com.codsoft.quizapp.exception.ResourceNotFoundException;
import com.codsoft.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public void setEnabled(Long id, boolean enabled) {
        User user = getUserOrThrow(id);
        user.setEnabled(enabled);
        userRepository.save(user);
    }
}
