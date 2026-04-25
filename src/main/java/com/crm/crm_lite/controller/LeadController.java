package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.service.LeadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService service;

    public LeadController(LeadService service) {
        this.service = service;
    }

    // helper — extracts User set by JwtFilter
    private User currentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    // GET /api/leads — Get all leads
    @GetMapping
    public ResponseEntity<List<Lead>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /api/leads/{id} — Get a single lead by ID
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // POST /api/leads — Create a new lead, owner = logged-in user
    @PostMapping
    public ResponseEntity<Lead> create(@RequestBody Lead lead, Authentication auth) {
        User user = currentUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(lead, user));
    }

    // PUT /api/leads/{id} — Only owner can update
    @PutMapping("/{id}")
    public ResponseEntity<Lead> update(@PathVariable Long id,
                                       @RequestBody Lead lead,
                                       Authentication auth) {
        User user = currentUser(auth);
        return ResponseEntity.ok(service.update(id, lead, user));
    }

    // DELETE /api/leads/{id} — Only owner can delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        User user = currentUser(auth);
        service.delete(id, user);
        return ResponseEntity.ok("Lead with ID " + id + " deleted successfully.");
    }
}