package com.crm.crm_lite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
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