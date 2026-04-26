package com.crm.crm_lite.service;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.model.User;
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
        this.noteRepo    = noteRepo;
        this.leadService = leadService;
    }

    @Transactional(readOnly = true)
    public List<Note> getByLeadId(Long leadId) {
        leadService.getById(leadId);
        return noteRepo.findByLeadId(leadId);
    }

    // FIX: removed ownership check — any logged-in user can add a note to any lead
    @Transactional
    public Note addNote(Long leadId, String content, User currentUser) {
        Lead lead = leadService.getById(leadId);

        Note note = new Note();
        note.setContent(content);
        note.setLead(lead);
        note.setCreatedBy(currentUser);
        return noteRepo.save(note);
    }

    // FIX: new method — only note creator can edit their own note
     
    // only note creator can delete
    @Transactional
    public void delete(Long noteId, User currentUser) {
        Note note = noteRepo.findById(noteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found"));

        if (!note.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot delete this note");
        }
        noteRepo.deleteById(noteId);
    }

    public List<Note> getAll() { return noteRepo.findAll(); }

    public Note getById(Long id) {
        return noteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found with ID: " + id));
    }
}