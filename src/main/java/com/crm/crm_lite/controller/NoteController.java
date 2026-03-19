package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    // GET /api/notes — Get all notes
    @GetMapping
    public ResponseEntity<List<Note>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /api/notes/{id} — Get a single note by ID
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /api/notes/lead/{leadId} — Get all notes for a specific lead
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Note>> getByLeadId(@PathVariable Long leadId) {
        return ResponseEntity.ok(service.getByLeadId(leadId));
    }

    // POST /api/notes/lead/{leadId} — Create a note for a specific lead (ID auto-generated)
    @PostMapping("/lead/{leadId}")
    public ResponseEntity<Note> create(@PathVariable Long leadId, @RequestBody Note note) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(leadId, note));
    }

    // PUT /api/notes/{id} — Update a note by ID
    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @RequestBody Note note) {
        return ResponseEntity.ok(service.update(id, note));
    }

    // DELETE /api/notes/{id} — Delete a note by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Note with ID " + id + " deleted successfully.");
    }
}