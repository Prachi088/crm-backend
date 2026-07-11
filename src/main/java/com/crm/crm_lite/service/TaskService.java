package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.TaskDto;
import com.crm.crm_lite.model.Customer;
import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.Task;
import com.crm.crm_lite.model.User;
import com.crm.crm_lite.repository.CustomerRepository;
import com.crm.crm_lite.repository.LeadRepository;
import com.crm.crm_lite.repository.TaskRepository;
import com.crm.crm_lite.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repo;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;

    public TaskService(TaskRepository repo, UserRepository userRepository, CustomerRepository customerRepository, LeadRepository leadRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
    }

    // Converts a Task entity into a plain field map before it leaves the service layer.
    // Task has lazy assignedUser/relatedCustomer/relatedLead associations, and Spring's
    // open-in-view is disabled, so the Hibernate session is already closed by the time
    // Jackson serializes the HTTP response. Returning the raw entity makes Jackson try to
    // initialize those proxies outside the session, which throws
    // "could not initialize proxy ... no Session". Only IDs are read here (safe — already
    // known on the proxy, doesn't trigger a lazy load), never the related entities themselves.
    private java.util.Map<String, Object> toResponseMap(Task task) {
        java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
        item.put("id", task.getId());
        item.put("title", task.getTitle());
        item.put("description", task.getDescription());
        item.put("dueDate", task.getDueDate());
        item.put("priority", task.getPriority());
        item.put("status", task.getStatus());
        item.put("completed", task.isCompleted());
        item.put("assignedUserId", task.getAssignedUser() != null ? task.getAssignedUser().getId() : null);
        item.put("relatedCustomerId", task.getRelatedCustomer() != null ? task.getRelatedCustomer().getId() : null);
        item.put("relatedLeadId", task.getRelatedLead() != null ? task.getRelatedLead().getId() : null);
        item.put("createdAt", task.getCreatedAt());
        item.put("updatedAt", task.getUpdatedAt());
        return item;
    }

    @Transactional(readOnly = true)
    public Page<java.util.Map<String, Object>> search(String q, String status, String priority, String completed, int page, int size) {
        String query = q == null ? "" : q.trim();
        String statusFilter = status == null ? "" : status.trim();
        String priorityFilter = priority == null ? "" : priority.trim();
        Boolean completedFilter = completed == null || completed.isBlank() ? null : Boolean.parseBoolean(completed);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
        return repo.search(query, statusFilter, priorityFilter, completedFilter, pageable)
                .map(this::toResponseMap);
    }

    // Used by GET /api/tasks/upcoming — the next N incomplete tasks, soonest due date first.
    // Returns plain field maps (not raw Task entities) to avoid LazyInitializationException
    // on the lazy assignedUser/relatedCustomer/relatedLead associations once the
    // transaction/session has closed and Jackson tries to serialize the response.
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getUpcoming(int limit) {
        int safeLimit = Math.min(50, Math.max(1, limit));
        Pageable pageable = PageRequest.of(0, safeLimit);
        return repo.findUpcoming(pageable).getContent().stream()
                .map(task -> {
                    java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", task.getId());
                    item.put("title", task.getTitle());
                    item.put("description", task.getDescription());
                    item.put("dueDate", task.getDueDate());
                    item.put("priority", task.getPriority());
                    item.put("status", task.getStatus());
                    item.put("completed", task.isCompleted());
                    return item;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getById(Long id) {
        return toResponseMap(findTaskOrThrow(id));
    }

    private Task findTaskOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Transactional
    public java.util.Map<String, Object> create(TaskDto dto) {
        Task task = map(dto, new Task());
        return toResponseMap(repo.save(task));
    }

    @Transactional
    public java.util.Map<String, Object> update(Long id, TaskDto dto) {
        Task task = findTaskOrThrow(id);
        map(dto, task);
        return toResponseMap(repo.save(task));
    }

    // Used by PATCH /api/tasks/{id}/status — quick status-only update from the
    // Tasks page dropdown, without requiring the full TaskDto payload.
    @Transactional
    public java.util.Map<String, Object> updateStatus(Long id, String status) {
        Task task = findTaskOrThrow(id);
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        task.setStatus(status);
        task.setCompleted("Completed".equalsIgnoreCase(status));
        return toResponseMap(repo.save(task));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        repo.deleteById(id);
    }

    private Task map(TaskDto dto, Task task) {
        if (dto.title != null) task.setTitle(dto.title.trim());
        task.setDescription(dto.description);
        if (dto.assignedUserId != null) {
            User user = userRepository.findById(dto.assignedUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigned user not found"));
            task.setAssignedUser(user);
        }
        if (dto.relatedCustomerId != null) {
            Customer customer = customerRepository.findById(dto.relatedCustomerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
            task.setRelatedCustomer(customer);
        }
        if (dto.relatedLeadId != null) {
            Lead lead = leadRepository.findById(dto.relatedLeadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
            task.setRelatedLead(lead);
        }
        task.setDueDate(dto.dueDate);
        task.setPriority(dto.priority == null || dto.priority.isBlank() ? "Medium" : dto.priority);
        task.setStatus(dto.status == null || dto.status.isBlank() ? "Pending" : dto.status);
        task.setCompleted(dto.completed);
        return task;
    }
}