package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.AuthResponse;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.UserRepository;
import com.crm.crm_lite.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        return email.trim().toLowerCase();
    }

    private String normalizePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        return password.trim();
    }

    public AuthResponse register(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPassword = normalizePassword(password);

        if (repo.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(encoder.encode(normalizedPassword));

        User saved = repo.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getId());
        return new AuthResponse(token, saved.getId(), saved.getEmail());
    }

    public AuthResponse login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPassword = normalizePassword(password);

        User user = repo.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        if (!encoder.matches(normalizedPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}
