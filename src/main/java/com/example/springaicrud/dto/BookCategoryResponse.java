package com.example.springaicrud.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCategoryResponse {
    private Long   id;
    private String category;
}