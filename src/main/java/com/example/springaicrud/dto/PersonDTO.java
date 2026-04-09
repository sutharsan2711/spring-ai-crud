package com.example.springaicrud.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO {
    private Long id;
    private String name;
    private String mobileNo;
    private String address;
    private String imageName;
    private String imageType;
    private String imageBase64;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}