package com.crm.crm_lite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

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

    @Column(updatable = false)
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

    public void setOwner(User owner)          { this.owner = owner; }
}