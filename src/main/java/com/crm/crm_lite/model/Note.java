package com.crm.crm_lite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    // FIX: @JsonIgnoreProperties prevents lazy-load crash and avoids serializing
    // the entire Lead object inside every Note response
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @JsonIgnore   // ✅ THIS is the actual fix
    private Lead lead;

    public Note() {}

    public Note(Long id, String content, Lead lead) {
        this.id = id;
        this.content = content;
        this.lead = lead;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }
}