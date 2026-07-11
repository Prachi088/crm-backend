package com.crm.crm_lite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CustomerDto {
    public Long id;

    @NotBlank(message = "Name is required")
    public String name;

    public String company;

    @Email(message = "Please provide a valid email")
    public String email;

    public String phone;
    public String address;
    public String industry;
    public String assignedSalesRepresentative;
    public String status;
}
