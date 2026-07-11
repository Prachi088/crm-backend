package com.crm.crm_lite.service;

import com.crm.crm_lite.dto.DashboardSummaryResponse;
import com.crm.crm_lite.model.Lead;
import com.crm.crm_lite.model.Task;
import com.crm.crm_lite.repository.CustomerRepository;
import com.crm.crm_lite.repository.LeadRepository;
import com.crm.crm_lite.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final TaskRepository taskRepository;

    public DashboardService(CustomerRepository customerRepository, LeadRepository leadRepository, TaskRepository taskRepository) {
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.taskRepository = taskRepository;
    }

    // Mirrors LEGACY_STATUS_MAP in the frontend's constants/crm.js — some leads
    // were created/migrated with old-style status values (e.g. "PROSPECT",
    // "CLOSED WON") instead of the current canonical names. Without this
    // normalization, getSummary()'s status-bucket counts silently come out
    // as zero for any lead still carrying a legacy value.
    private static final Map<String, String> LEGACY_STATUS_MAP = Map.of(
            "PROSPECT", "New",
            "QUALIFIED", "Qualified",
            "PROPOSAL", "Proposal Sent",
            "CLOSED WON", "Won",
            "CLOSED LOST", "Lost"
    );

    private String normalizeLeadStatus(String status) {
        if (status == null || status.isBlank()) return "New";
        return LEGACY_STATUS_MAP.getOrDefault(status, status);
    }

    public DashboardSummaryResponse getSummary() {
        long totalCustomers = customerRepository.count();
        List<Lead> allLeads = leadRepository.findAll();
        long totalLeads = allLeads.size();

        // Active deals = leads not yet closed (excludes Won/Lost), using normalized status
        long activeDeals = allLeads.stream()
                .filter(lead -> {
                    String normalized = normalizeLeadStatus(lead.getStatus());
                    return !"Won".equals(normalized) && !"Lost".equals(normalized);
                })
                .count();

        // Pending tasks = tasks not yet completed
        long pendingTasks = taskRepository.findAll().stream()
                .filter(task -> !task.isCompleted())
                .count();

        // Revenue: prefer expectedRevenue, fall back to dealValue, same as the
        // frontend's offline fallback calculation used before this endpoint existed.
        double totalRevenue = allLeads.stream()
                .mapToDouble(lead -> {
                    Double value = lead.getExpectedRevenue() != null ? lead.getExpectedRevenue() : lead.getDealValue();
                    return value == null ? 0 : value;
                })
                .sum();

        Map<String, Long> leadStatusCounts = new LinkedHashMap<>();
        for (String status : List.of("New", "Contacted", "Qualified", "Proposal Sent", "Negotiation", "Won", "Lost")) {
            long count = allLeads.stream().filter(lead -> status.equals(normalizeLeadStatus(lead.getStatus()))).count();
            leadStatusCounts.put(status, count);
        }

        List<Map<String, Object>> monthlyLeadStats = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String label = month.getMonth().name().substring(0, 3) + " " + month.getYear();
            long count = allLeads.stream()
                    .filter(lead -> lead.getCreatedAt() != null && lead.getCreatedAt().toLocalDate().getMonth() == month.getMonth() && lead.getCreatedAt().toLocalDate().getYear() == month.getYear())
                    .count();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", label);
            item.put("count", count);
            monthlyLeadStats.add(item);
        }

        List<Map<String, Object>> recentActivities = new ArrayList<>();
        List<Lead> recentLeads = allLeads.stream().limit(5).toList();
        for (Lead lead : recentLeads) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", "lead");
            item.put("message", "New lead created: " + lead.getName());
            item.put("createdAt", lead.getCreatedAt());
            recentActivities.add(item);
        }
        List<Task> pendingTaskItems = taskRepository.findAll().stream().filter(task -> !task.isCompleted()).limit(5).toList();
        for (Task task : pendingTaskItems) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", "task");
            item.put("message", "Task pending: " + task.getTitle());
            item.put("createdAt", task.getCreatedAt());
            recentActivities.add(item);
        }

        return new DashboardSummaryResponse(totalCustomers, totalLeads, activeDeals, pendingTasks, totalRevenue, leadStatusCounts, monthlyLeadStats, recentActivities);
    }
}