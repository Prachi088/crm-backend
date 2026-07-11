package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.TaskDto;
import com.crm.crm_lite.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<java.util.Map<String, Object>>> getAll(@RequestParam(defaultValue = "") String q,
                                                                      @RequestParam(defaultValue = "") String status,
                                                                      @RequestParam(defaultValue = "") String priority,
                                                                      @RequestParam(defaultValue = "") String completed,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.search(q, status, priority, completed, page, size));
    }

    // GET /api/tasks/upcoming — used by the Dashboard's fetchUpcomingTasks().
    // IMPORTANT: this must stay declared above @GetMapping("/{id}") below.
    // If it were declared after, Spring would route "/api/tasks/upcoming" into
    // getById(@PathVariable Long id) and try Long.parseLong("upcoming"), which
    // is exactly the bug this endpoint fixes.
    @GetMapping("/upcoming")
    public ResponseEntity<List<java.util.Map<String, Object>>> getUpcoming(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(service.getUpcoming(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<java.util.Map<String, Object>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<java.util.Map<String, Object>> create(@Valid @RequestBody TaskDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<java.util.Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody TaskDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // PATCH /api/tasks/{id}/status — used by the status dropdown on the Tasks page.
    @PatchMapping("/{id}/status")
    public ResponseEntity<java.util.Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(service.updateStatus(id, body.get("status")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}