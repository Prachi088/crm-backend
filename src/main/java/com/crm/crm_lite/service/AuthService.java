package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.AuthResponse;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.UserRepository;
import com.crm.crm_lite.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo, JwtUtil jwtUtil) {
        this.repo    = repo;
        this.jwtUtil = jwtUtil;
    }

    // FIX: now returns AuthResponse (token + userId + email) instead of just a token string.
    // This lets the frontend store userId so it can compare with lead.owner.id.
    public AuthResponse register(String email, String password) {
        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(encoder.encode(password));
        User saved = repo.save(user);                          // ← use saved, not user
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getId());
        return new AuthResponse(token, saved.getId(), saved.getEmail());
    }

    public AuthResponse login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        User user = repo.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}