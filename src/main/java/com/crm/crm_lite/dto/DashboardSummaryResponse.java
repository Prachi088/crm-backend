package com.crm.crm_lite.dto;

import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {
    public long totalCustomers;
    public long totalLeads;
    public long activeDeals;
    public long pendingTasks;
    public double totalRevenue;
    public Map<String, Long> leadStatusCounts;
    public List<Map<String, Object>> monthlyLeadStats;
    public List<Map<String, Object>> recentActivities;

    public DashboardSummaryResponse(long totalCustomers, long totalLeads, long activeDeals, long pendingTasks,
                                    double totalRevenue, Map<String, Long> leadStatusCounts,
                                    List<Map<String, Object>> monthlyLeadStats,
                                    List<Map<String, Object>> recentActivities) {
        this.totalCustomers = totalCustomers;
        this.totalLeads = totalLeads;
        this.activeDeals = activeDeals;
        this.pendingTasks = pendingTasks;
        this.totalRevenue = totalRevenue;
        this.leadStatusCounts = leadStatusCounts;
        this.monthlyLeadStats = monthlyLeadStats;
        this.recentActivities = recentActivities;
    }
}
