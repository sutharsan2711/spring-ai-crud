package com.example.springaicrud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String category;
}