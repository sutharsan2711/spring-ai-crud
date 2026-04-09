package com.example.springaicrud.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long   id;
    private String name;
    private String email;
    private Long   roleId;
    private String roleName;
    private String token;
    private String message;

    private Date loginTime;
    private Date   expTime;
}