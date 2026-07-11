package com.crm.crm_lite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String company;
    private Double dealValue;
    private String status;
    private String leadSource;
    private String assignedSalesRepresentative;
    private Double expectedRevenue;
    private LocalDate followUpDate;
    private String priority;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")

    private LocalDateTime createdAt;

    // ownership
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User owner;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public void setId(Long id)                { this.id = id; }

    public void setName(String name)          { this.name = name; }

    public void setEmail(String email)        { this.email = email; }

    public void setCompany(String c)          { this.company = c; }

    public void setDealValue(Double d)        { this.dealValue = d; }

    public void setStatus(String s)           { this.status = s; }

    public String getLeadSource() { return leadSource; }
    public void setLeadSource(String leadSource) { this.leadSource = leadSource; }
    public String getAssignedSalesRepresentative() { return assignedSalesRepresentative; }
    public void setAssignedSalesRepresentative(String assignedSalesRepresentative) { this.assignedSalesRepresentative = assignedSalesRepresentative; }
    public Double getExpectedRevenue() { return expectedRevenue; }
    public void setExpectedRevenue(Double expectedRevenue) { this.expectedRevenue = expectedRevenue; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public void setOwner(User owner)          { this.owner = owner; }
}