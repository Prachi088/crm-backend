package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    // helper — extracts User set by JwtFilter
    private User currentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    // GET /api/notes/lead/{leadId}
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Note>> getByLeadId(@PathVariable Long leadId) {
        return ResponseEntity.ok(service.getByLeadId(leadId));
    }

    // POST /api/notes/lead/{leadId}  ← owner only
    @PostMapping("/lead/{leadId}")
    public ResponseEntity<Note> create(@PathVariable Long leadId,
                                       @RequestBody Map<String, String> body,
                                       Authentication auth) {
        User user    = currentUser(auth);
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content is required");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addNote(leadId, content, user));
    }

    // DELETE /api/notes/{id}  ← note creator only
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, currentUser(auth));
        return ResponseEntity.ok("Note deleted");
    }

    // GET /api/notes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
}