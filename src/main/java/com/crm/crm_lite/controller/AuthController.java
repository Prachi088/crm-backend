package com.crm.crm_lite.controller;

import com.crm.crm_lite.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crm.crm_lite.dto.AuthRequest;
import com.crm.crm_lite.dto.AuthResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest req) {
        return new AuthResponse(service.register(req.email, req.password));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            String token = service.login(req.email, req.password);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Invalid email or password")
            );
        }
    }
}