package com.example.springaicrud.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email like john@gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6,
            message = "Password must be at least 6 characters")
    private String password;
}