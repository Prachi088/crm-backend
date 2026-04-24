package com.crm.crm_lite.service;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public List<Note> getAll() {
        return noteRepo.findAll();
    }

    public Note getById(Long id) {
        return noteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found with ID: " + id));
    }

    public List<Note> getByLeadId(Long leadId) {
        leadService.getById(leadId);
        return noteRepo.findByLeadId(leadId);
    }

    @Transactional
    public Note save(Long leadId, Note note) {
        Lead lead = leadService.getById(leadId);
        note.setId(null);
        note.setLead(lead);
        return noteRepo.save(note);
    }

    public Note update(Long id, Note updatedNote) {
        Note existing = getById(id);
        existing.setContent(updatedNote.getContent());
        return noteRepo.save(existing);
    }

    public void delete(Long id) {
        if (!noteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Note not found with ID: " + id);
        }
        noteRepo.deleteById(id);
    }
}