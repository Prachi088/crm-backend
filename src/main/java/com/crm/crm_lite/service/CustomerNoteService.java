package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.NoteDto;
import com.crm.crm_lite.model.Customer;
import com.crm.crm_lite.model.Note;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.CustomerRepository;
import com.crm.crm_lite.repository.NoteRepository;
import com.crm.crm_lite.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerNoteService {

    private final NoteRepository noteRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerNoteService(NoteRepository noteRepository, CustomerRepository customerRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Note> getByCustomerId(Long customerId) {
        return noteRepository.findAll().stream()
                .filter(note -> note.getLead() == null && note.getCreatedBy() != null)
                .toList();
    }

    @Transactional
    public Note create(Long customerId, NoteDto dto, User currentUser) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Note note = new Note();
        note.setContent(dto.content);
        note.setCreatedBy(currentUser);
        note.setLead(null);
        note.setCustomer(customer);
        return noteRepository.save(note);
    }

    @Transactional
    public Note update(Long customerId, Long id, NoteDto dto, User currentUser) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        note.setContent(dto.content);
        return noteRepository.save(note);
    }

    @Transactional
    public void delete(Long customerId, Long id, User currentUser) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        noteRepository.delete(note);
    }
}
