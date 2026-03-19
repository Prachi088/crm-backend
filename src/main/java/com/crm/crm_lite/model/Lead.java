package com.crm.crm_lite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String company;
    private String status;
    private Double dealValue;  // NEW FIELD

    public Lead() {}

    public Lead(Long id, String name, String email, String company, String status, Double dealValue) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.company = company;
        this.status = status;
        this.dealValue = dealValue;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getDealValue() { return dealValue; }
    public void setDealValue(Double dealValue) { this.dealValue = dealValue; }
}