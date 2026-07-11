package com.crm.crm_lite.service;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.NoteRepository;
import org.springframework.cache.annotation.CacheEvict;
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

    @Transactional(readOnly = true)
    public List<Note> getByCustomerId(Long customerId) {
        return noteRepo.findAll().stream()
                .filter(note -> note.getCustomer() != null && customerId.equals(note.getCustomer().getId()))
                .toList();
    }

    // any logged-in user can add a note to any lead
    @Transactional
    @CacheEvict(value = "leads", key = "'all'")
    public Note addNote(Long leadId, String content, User currentUser) {
        Lead lead = leadService.getById(leadId);

        Note note = new Note();
        note.setContent(content);
        note.setLead(lead);
        note.setCreatedBy(currentUser);
        return noteRepo.save(note);
    }

    // only note creator can edit their own note
    @Transactional
    @CacheEvict(value = "leads", key = "'all'")
    public Note update(Long noteId, String content, User currentUser) {
        Note note = noteRepo.findByIdWithCreatedBy(noteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found"));

        if (note.getCreatedBy() == null ||
                !note.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot edit this note");
        }

        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Content cannot be empty");
        }

        note.setContent(content.trim());
        return noteRepo.save(note);
    }

    // only note creator can delete
    @Transactional
    @CacheEvict(value = "leads", key = "'all'")
    public void delete(Long noteId, User currentUser) {
        Note note = noteRepo.findByIdWithCreatedBy(noteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found"));

        if (note.getCreatedBy() == null ||
                !note.getCreatedBy().getId().equals(currentUser.getId())) {
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