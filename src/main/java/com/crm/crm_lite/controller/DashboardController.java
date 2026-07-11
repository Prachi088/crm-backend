package com.crm.crm_lite.controller;

import com.crm.crm_lite.dto.DashboardSummaryResponse;
import com.crm.crm_lite.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    // GET /api/dashboard — kept for backward compatibility
    @GetMapping
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }

    // GET /api/dashboard/summary — used by the frontend's fetchDashboardSummary()
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummaryAlias() {
        return ResponseEntity.ok(service.getSummary());
    }

    // GET /api/dashboard/activities — used by the frontend's fetchDashboardActivities()
    @GetMapping("/activities")
    public ResponseEntity<List<Map<String, Object>>> getActivities() {
        return ResponseEntity.ok(service.getSummary().recentActivities);
    }
}