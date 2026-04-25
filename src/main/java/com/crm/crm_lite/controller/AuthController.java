package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.AuthRequest;
import com.crm.crm_lite.dto.AuthResponse;
import com.crm.crm_lite.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        try {
            AuthResponse res = service.register(req.email, req.password);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(409).body(
                    Map.of("error", "Email already registered")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            AuthResponse res = service.login(req.email, req.password);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Invalid email or password")
            );
        }
    }
}