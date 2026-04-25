package com.crm.crm_lite.service;

import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Returns the current user — identity always comes from JWT, never from request body
    public User getMe(User currentUser) {
        return userRepo.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Updates only the current user's own data
    // Accepts a map so we can update only the fields that are sent
    public User updateMe(User currentUser, Map<String, String> updates) {
        User user = userRepo.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update email if provided and not already taken by another user
        if (updates.containsKey("email")) {
            String newEmail = updates.get("email").trim().toLowerCase();
            if (!newEmail.equals(user.getEmail())) {
                boolean taken = userRepo.findByEmail(newEmail)
                        .filter(u -> !u.getId().equals(user.getId()))
                        .isPresent();
                if (taken) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
                }
                user.setEmail(newEmail);
            }
        }

        // Update password if provided
        if (updates.containsKey("password") && !updates.get("password").isBlank()) {
            user.setPassword(encoder.encode(updates.get("password")));
        }

        return userRepo.save(user);
    }
}