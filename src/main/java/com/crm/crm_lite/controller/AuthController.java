package com.crm.crm_lite.controller;

import com.crm.crm_lite.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.crm.crm_lite.dto.AuthRequest;
import com.crm.crm_lite.dto.AuthResponse;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
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
    public AuthResponse login(@RequestBody AuthRequest req) {
        return new AuthResponse(service.login(req.email, req.password));
    }
}