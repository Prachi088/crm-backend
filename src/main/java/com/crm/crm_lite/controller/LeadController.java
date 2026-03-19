package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.service.LeadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadController {

    private final LeadService service;

    public LeadController(LeadService service) {
        this.service = service;
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

    // POST /api/leads — Create a new lead (ID is auto-generated)
    @PostMapping
    public ResponseEntity<Lead> create(@RequestBody Lead lead) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(lead));
    }

    // PUT /api/leads/{id} — Update an existing lead by ID
    @PutMapping("/{id}")
    public ResponseEntity<Lead> update(@PathVariable Long id, @RequestBody Lead lead) {
        return ResponseEntity.ok(service.update(id, lead));
    }

    // DELETE /api/leads/{id} — Delete a lead by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Lead with ID " + id + " deleted successfully.");
    }
}