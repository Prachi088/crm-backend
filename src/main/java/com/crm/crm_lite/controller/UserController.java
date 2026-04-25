package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.User;
import com.crm.crm_lite.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Extracts the User set by JwtFilter — identity always from token, never from URL param
    private User currentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    // GET /api/users/me — returns current user's profile
    // Never exposes password
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(Authentication auth) {
        User user = userService.getMe(currentUser(auth));
        return ResponseEntity.ok(Map.of(
                "id",    user.getId(),
                "email", user.getEmail()
        ));
    }

    // PUT /api/users/me — updates current user's own data only
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMe(
            @RequestBody Map<String, String> updates,
            Authentication auth) {
        User updated = userService.updateMe(currentUser(auth), updates);
        return ResponseEntity.ok(Map.of(
                "id",    updated.getId(),
                "email", updated.getEmail()
        ));
    }
}