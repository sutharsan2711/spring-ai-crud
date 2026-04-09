package com.example.springaicrud.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email like john@gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}