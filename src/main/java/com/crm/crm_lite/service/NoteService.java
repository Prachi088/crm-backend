package com.crm.crm_lite.service;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepo;
    private final LeadService leadService;

    public NoteService(NoteRepository noteRepo, LeadService leadService) {
        this.noteRepo = noteRepo;
        this.leadService = leadService;
    }

    // GET all notes
    public List<Note> getAll() {
        return noteRepo.findAll();
    }

    // GET note by ID
    public Note getById(Long id) {
        return noteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found with ID: " + id));
    }

    // GET all notes for a specific lead
    public List<Note> getByLeadId(Long leadId) {
        leadService.getById(leadId); // validates lead exists (throws 404 if not)
        return noteRepo.findByLeadId(leadId);
    }

    // POST - create note linked to a lead
    public Note save(Long leadId, Note note) {
        Lead lead = leadService.getById(leadId); // validates lead exists
        note.setId(null); // Force auto-increment ID
        note.setLead(lead);
        return noteRepo.save(note);
    }

    // PUT - update note by ID
    public Note update(Long id, Note updatedNote) {
        Note existing = getById(id);
        existing.setContent(updatedNote.getContent());
        return noteRepo.save(existing);
    }

    // DELETE by ID
    public void delete(Long id) {
        if (!noteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Note not found with ID: " + id);
        }
        noteRepo.deleteById(id);
    }
}