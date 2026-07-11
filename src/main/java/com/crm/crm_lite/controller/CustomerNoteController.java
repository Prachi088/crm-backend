package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.NoteDto;
import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.service.CustomerNoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/notes")
public class CustomerNoteController {

    private final CustomerNoteService service;

    public CustomerNoteController(CustomerNoteService service) {
        this.service = service;
    }

    private com.crm.crm_lite.model.User currentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof com.crm.crm_lite.model.User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return (com.crm.crm_lite.model.User) auth.getPrincipal();
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAll(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<Note> create(@PathVariable Long customerId, @Valid @RequestBody NoteDto dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(customerId, dto, currentUser(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long customerId, @PathVariable Long id, @Valid @RequestBody NoteDto dto, Authentication auth) {
        return ResponseEntity.ok(service.update(customerId, id, dto, currentUser(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long customerId, @PathVariable Long id, Authentication auth) {
        service.delete(customerId, id, currentUser(auth));
        return ResponseEntity.noContent().build();
    }
}
