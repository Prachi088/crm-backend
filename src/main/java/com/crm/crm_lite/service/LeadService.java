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

    public List<Lead> getAll() {
        return repo.findAll();
    }

    public Lead getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lead not found with ID: " + id));
    }

    public Lead save(Lead lead) {
        lead.setId(null);

        if (lead.getEmail() != null) {
            lead.setEmail(lead.getEmail().trim().toLowerCase());
        }

        if (repo.existsByEmailIgnoreCase(lead.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A lead with this email already exists.");
        }

        return repo.save(lead);
    }

    public Lead update(Long id, Lead updatedLead) {
        Lead existing = getById(id);

        String newEmail = updatedLead.getEmail() != null
                ? updatedLead.getEmail().trim().toLowerCase()
                : null;

        if (newEmail != null &&
                !existing.getEmail().equalsIgnoreCase(newEmail) &&
                repo.existsByEmailIgnoreCase(newEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Another lead with this email already exists.");
        }

        existing.setName(updatedLead.getName());
        existing.setEmail(newEmail);
        existing.setCompany(updatedLead.getCompany());
        existing.setStatus(updatedLead.getStatus());
        existing.setDealValue(updatedLead.getDealValue());

        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Lead not found with ID: " + id);
        }
        repo.deleteById(id);
    }
}