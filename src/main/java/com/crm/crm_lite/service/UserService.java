package com.crm.crm_lite.service;

import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // Used by LeadForm to populate the "Assigned Sales Representative" dropdown
    public List<User> getAll() {
        return repo.findAll();
    }

    public User getMe(User user) {
        return repo.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Find any user by ID (for public profile viewing)
    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User updateMe(User current, Map<String, String> updates) {
        User user = repo.findById(current.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Only password can be updated — email is immutable
        String newPassword = updates.get("password");
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.trim().length() < 6) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
            }
            user.setPassword(encoder.encode(newPassword.trim()));
        }

        return repo.save(user);
    }
}