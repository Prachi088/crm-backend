package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.PagedLeadsResponse;
import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.LeadRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private boolean isAdmin(User user) {
        return "ROLE_ADMIN".equals(user.getRole());
    }

    // ── READ — all leads cached (used by analytics + chat) ────────
    @Cacheable(value = "leads", key = "'all'")
    public List<Lead> getAll() {
        return repo.findAllWithOwner();
    }

    // ── READ — paginated + search (not cached — dynamic query) ────
    public PagedLeadsResponse search(String q, String status, int page, int size, String sort) {
        String query        = (q == null || q.isBlank()) ? "" : q.trim();
        String statusFilter = (status == null || status.isBlank()) ? "ALL" : status.trim();

        Sort sortObj = sort != null && sort.equals("dealValue")
                ? Sort.by(Sort.Direction.DESC, "dealValue")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.min(50, Math.max(1, size)),
                sortObj);

        Page<Lead> result = repo.search(query, statusFilter, pageable);

        return new PagedLeadsResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast());
    }

    // ── not cached — used internally for ownership checks ─────────
    public Lead getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lead not found with ID: " + id));
    }

    public Lead getByIdWithOwner(Long id) {
        return repo.findByIdWithOwner(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lead not found with ID: " + id));
    }

    // ── WRITE — evict all-leads cache ─────────────────────────────
    @CacheEvict(value = "leads", key = "'all'")
    public Lead save(Lead lead, User owner) {
        lead.setId(null);
        lead.setOwner(owner);
        lead.setLeadSource(lead.getLeadSource());
        lead.setAssignedSalesRepresentative(lead.getAssignedSalesRepresentative());
        lead.setExpectedRevenue(lead.getExpectedRevenue());
        lead.setFollowUpDate(lead.getFollowUpDate());
        lead.setPriority(lead.getPriority());

        if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
            lead.setEmail(lead.getEmail().trim().toLowerCase());
            if (repo.existsByEmailIgnoreCase(lead.getEmail())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "A lead with this email already exists.");
            }
        }

        return repo.save(lead);
    }

    @CacheEvict(value = "leads", key = "'all'")
    public Lead update(Long id, Lead updatedLead, User currentUser) {
        Lead existing = getByIdWithOwner(id);

        if (!isAdmin(currentUser)) {
            if (existing.getOwner() == null ||
                    !existing.getOwner().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You are not the owner of this lead");
            }
        }

        String newEmail = updatedLead.getEmail() != null
                ? updatedLead.getEmail().trim().toLowerCase()
                : null;

        if (newEmail != null &&
                !newEmail.equalsIgnoreCase(existing.getEmail()) &&
                repo.existsByEmailIgnoreCase(newEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Another lead with this email already exists.");
        }

        existing.setName(updatedLead.getName());
        existing.setEmail(newEmail);
        existing.setCompany(updatedLead.getCompany());
        existing.setStatus(updatedLead.getStatus());
        existing.setDealValue(updatedLead.getDealValue());
        existing.setLeadSource(updatedLead.getLeadSource());
        existing.setAssignedSalesRepresentative(updatedLead.getAssignedSalesRepresentative());
        existing.setExpectedRevenue(updatedLead.getExpectedRevenue());
        existing.setFollowUpDate(updatedLead.getFollowUpDate());
        existing.setPriority(updatedLead.getPriority());

        return repo.save(existing);
    }

    @CacheEvict(value = "leads", key = "'all'")
    public void delete(Long id, User currentUser) {
        Lead existing = getByIdWithOwner(id);

        if (!isAdmin(currentUser)) {
            if (existing.getOwner() == null ||
                    !existing.getOwner().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You are not the owner of this lead");
            }
        }

        repo.deleteById(id);
    }
}