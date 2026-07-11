package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.PagedLeadsResponse;
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

    private User currentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    // GET /api/leads — all leads (cached, used by analytics + chat)
    @GetMapping
    public ResponseEntity<List<Lead>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /api/leads/search?q=alice&status=ALL&page=0&size=20&sort=createdAt
    // Paginated + searchable — used by LeadList UI
    @GetMapping("/search")
    public ResponseEntity<PagedLeadsResponse> search(
            @RequestParam(defaultValue = "")         String q,
            @RequestParam(defaultValue = "ALL")      String status,
            @RequestParam(defaultValue = "0")        int    page,
            @RequestParam(defaultValue = "20")       int    size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        return ResponseEntity.ok(service.search(q, status, page, size, sort));
    }

    // GET /api/leads/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // POST /api/leads
    @PostMapping
    public ResponseEntity<Lead> create(@RequestBody Lead lead, Authentication auth) {
        User user = currentUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(lead, user));
    }

    // PUT /api/leads/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Lead> update(@PathVariable Long id,
                                       @RequestBody Lead lead,
                                       Authentication auth) {
        User user = currentUser(auth);
        return ResponseEntity.ok(service.update(id, lead, user));
    }

    // DELETE /api/leads/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        User user = currentUser(auth);
        service.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}