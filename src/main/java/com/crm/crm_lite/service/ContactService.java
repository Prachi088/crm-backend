package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.ContactDto;
import com.crm.crm_lite.model.Contact;
import com.crm.crm_lite.model.Customer;
import com.crm.crm_lite.repository.ContactRepository;
import com.crm.crm_lite.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {

    private final ContactRepository repo;
    private final CustomerRepository customerRepository;

    public ContactService(ContactRepository repo, CustomerRepository customerRepository) {
        this.repo = repo;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Page<Contact> search(Long customerId, String q, int page, int size) {
        Customer customer = getCustomer(customerId);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
        return repo.searchByCustomer(customer.getId(), q == null ? "" : q.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Contact getById(Long customerId, Long id) {
        Contact contact = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        if (!contact.getCustomer().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found");
        }
        return contact;
    }

    @Transactional
    public Contact create(Long customerId, ContactDto dto) {
        Customer customer = getCustomer(customerId);
        Contact contact = map(dto, new Contact());
        contact.setCustomer(customer);
        return repo.save(contact);
    }

    @Transactional
    public Contact update(Long customerId, Long id, ContactDto dto) {
        Contact contact = getById(customerId, id);
        map(dto, contact);
        return repo.save(contact);
    }

    @Transactional
    public void delete(Long customerId, Long id) {
        Contact contact = getById(customerId, id);
        repo.delete(contact);
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    private Contact map(ContactDto dto, Contact contact) {
        if (dto.firstName != null) contact.setFirstName(dto.firstName.trim());
        if (dto.lastName != null) contact.setLastName(dto.lastName.trim());
        contact.setEmail(dto.email);
        contact.setPhone(dto.phone);
        contact.setDesignation(dto.designation);
        contact.setDepartment(dto.department);
        return contact;
    }
}
