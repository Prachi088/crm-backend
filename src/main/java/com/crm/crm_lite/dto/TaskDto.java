package com.crm.crm_lite.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class TaskDto {
    public Long id;

    @NotBlank(message = "Title is required")
    public String title;

    public String description;
    public Long assignedUserId;
    public Long relatedCustomerId;
    public Long relatedLeadId;
    public LocalDate dueDate;
    public String priority;
    public String status;
    public boolean completed;
}
