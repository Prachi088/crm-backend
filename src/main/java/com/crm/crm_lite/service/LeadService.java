package com.crm.crm_lite.service;

import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.repository.LeadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LeadService {

    private final LeadRepository repo;

    public LeadService(LeadRepository repo) {
        this.repo = repo;
    }

    // GET all leads
    public List<Lead> getAll() {
        return repo.findAll();
    }

    // GET lead by ID
    public Lead getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lead not found with ID: " + id));
    }

    // POST - create new lead (ID is auto-generated, never set manually)
    public Lead save(Lead lead) {
        lead.setId(null); // Force auto-increment — never allow manual ID on create
        if (repo.existsByEmail(lead.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A lead with this email already exists.");
        }
        return repo.save(lead);
    }

    // PUT - update existing lead by ID
    public Lead update(Long id, Lead updatedLead) {
        Lead existing = getById(id); // throws 404 if not found
        existing.setName(updatedLead.getName());
        existing.setEmail(updatedLead.getEmail());
        existing.setCompany(updatedLead.getCompany());
        existing.setStatus(updatedLead.getStatus());
        return repo.save(existing);
    }

    // DELETE by ID
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Lead not found with ID: " + id);
        }
        repo.deleteById(id);
    }
}