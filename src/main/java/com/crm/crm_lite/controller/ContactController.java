package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.ContactDto;
import com.crm.crm_lite.model.Contact;
import com.crm.crm_lite.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/{customerId}/contacts")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getAll(@PathVariable Long customerId,
                                                @RequestParam(defaultValue = "") String q,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.search(customerId, q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getById(@PathVariable Long customerId, @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(customerId, id));
    }

    @PostMapping
    public ResponseEntity<Contact> create(@PathVariable Long customerId, @Valid @RequestBody ContactDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(customerId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> update(@PathVariable Long customerId, @PathVariable Long id, @Valid @RequestBody ContactDto dto) {
        return ResponseEntity.ok(service.update(customerId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long customerId, @PathVariable Long id) {
        service.delete(customerId, id);
        return ResponseEntity.noContent().build();
    }
}
