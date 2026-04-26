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

    private User currentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    // GET /api/users/me — returns current user's profile (never exposes password)
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(Authentication auth) {
        User user = userService.getMe(currentUser(auth));
        return ResponseEntity.ok(Map.of(
                "id",    user.getId(),
                "email", user.getEmail()
        ));
    }

    // GET /api/users/{id} — returns any user's public profile (no password, no email)
    // Any logged-in user can view another user's basic info
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long id,
            Authentication auth) {
        // Must be logged in to view others' profiles
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User viewer = (User) auth.getPrincipal();
        User target = userService.findById(id);

        // If viewing own profile, return full info
        if (viewer.getId().equals(target.getId())) {
            return ResponseEntity.ok(Map.of(
                    "id",    target.getId(),
                    "email", target.getEmail()
            ));
        }

        // For others — return only id and masked name (first part of email)
        String maskedName = target.getEmail().split("@")[0];
        return ResponseEntity.ok(Map.of(
                "id",    target.getId(),
                "email", target.getEmail(),
                "name",  maskedName
        ));
    }

    // PUT /api/users/me — updates only password (email is immutable)
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMe(
            @RequestBody Map<String, String> updates,
            Authentication auth) {
        // Strip email from updates — email cannot be changed
        updates.remove("email");
        User updated = userService.updateMe(currentUser(auth), updates);
        return ResponseEntity.ok(Map.of(
                "id",    updated.getId(),
                "email", updated.getEmail()
        ));
    }
}