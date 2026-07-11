package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.Contact;
import com.crm.crm_lite.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Flat, cross-customer contact listing — backs the frontend's Contacts page
 * (searchContacts() in client.js), which calls GET /api/contacts?q=&customerId=&page=&size=&sort=
 *
 * This is separate from ContactController, which stays scoped under
 * /api/customers/{customerId}/contacts for customer-specific contact management.
 */
@RestController
@RequestMapping("/api/contacts")
public class ContactSearchController {

    private final ContactRepository contactRepository;

    public ContactSearchController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));

        Page<Contact> result = (customerId != null)
                ? contactRepository.searchByCustomer(customerId, q, pageable)
                : contactRepository.searchAll(q, pageable);

        return ResponseEntity.ok(result);
    }
}