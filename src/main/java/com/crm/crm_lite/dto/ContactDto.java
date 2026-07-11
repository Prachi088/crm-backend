package com.crm.crm_lite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ContactDto {
    public Long id;

    @NotBlank(message = "First name is required")
    public String firstName;

    @NotBlank(message = "Last name is required")
    public String lastName;

    @Email(message = "Please provide a valid email")
    public String email;

    public String phone;
    public String designation;
    public String department;
    public Long customerId;
}
