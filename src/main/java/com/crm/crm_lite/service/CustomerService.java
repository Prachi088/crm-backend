package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.CustomerDto;
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
public class CustomerService {

    private final CustomerRepository repo;
    private final ContactRepository contactRepository;
    private final com.crm.crm_lite.repository.TaskRepository taskRepository;

    public CustomerService(CustomerRepository repo, ContactRepository contactRepository, com.crm.crm_lite.repository.TaskRepository taskRepository) {
        this.repo = repo;
        this.contactRepository = contactRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Page<Customer> search(String q, String status, String industry, int page, int size) {
        String query = q == null ? "" : q.trim();
        String statusFilter = status == null ? "" : status.trim();
        String industryFilter = industry == null ? "" : industry.trim();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
        return repo.search(query, statusFilter, industryFilter, pageable);
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @Transactional
    public Customer create(CustomerDto dto) {
        Customer customer = map(dto, new Customer());
        return repo.save(customer);
    }

    @Transactional
    public Customer update(Long id, CustomerDto dto) {
        Customer customer = getById(id);
        map(dto, customer);
        return repo.save(customer);
    }

    // Deleting a customer must also detach everything that points back at it via a
    // foreign key, or the DB rejects the delete with a constraint violation:
    //  - Contact.customer_id is NOT NULL, so contacts are deleted outright.
    //  - Task.customer_id is nullable, so tasks are kept but unlinked instead.
    // Applies to every customer, regardless of whether it was created through the
    // UI or seeded directly into the database.
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        contactRepository.deleteByCustomerId(id);
        taskRepository.clearRelatedCustomer(id);
        repo.deleteById(id);
    }

    private Customer map(CustomerDto dto, Customer customer) {
        if (dto.name != null) customer.setName(dto.name.trim());
        customer.setCompany(dto.company);
        customer.setEmail(dto.email);
        customer.setPhone(dto.phone);
        customer.setAddress(dto.address);
        customer.setIndustry(dto.industry);
        customer.setAssignedSalesRepresentative(dto.assignedSalesRepresentative);
        customer.setStatus(dto.status == null || dto.status.isBlank() ? "Active" : dto.status);
        return customer;
    }
}