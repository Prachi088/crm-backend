package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.AuthRequest;
import com.crm.crm_lite.dto.AuthResponse;
import com.crm.crm_lite.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody(required = false) AuthRequest req) {
        if (req == null) {
            return error(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        try {
            AuthResponse res = service.register(req.email, req.password, req.role);
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            String message = ex.getReason() != null ? ex.getReason() : "Registration failed";
            return error(HttpStatus.valueOf(ex.getStatusCode().value()), message);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) AuthRequest req) {
        if (req == null) {
            return error(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        try {
            AuthResponse res = service.login(req.email, req.password);
            return ResponseEntity.ok(res);
        } catch (ResponseStatusException ex) {
            String message = ex.getReason() != null ? ex.getReason() : "Login failed";
            return error(HttpStatus.valueOf(ex.getStatusCode().value()), message);
        }
    }
}
