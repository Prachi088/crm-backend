package com.crm.crm_lite.dto;

import jakarta.validation.constraints.NotBlank;

public class NoteDto {
    public Long id;

    @NotBlank(message = "Content is required")
    public String content;

    public Long authorId;
    public Long customerId;
    public Long leadId;
}
