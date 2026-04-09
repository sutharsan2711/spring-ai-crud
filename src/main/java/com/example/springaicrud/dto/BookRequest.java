package com.example.springaicrud.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookRequest {

    @NotBlank(message = "Book name is required")
    private String name;

    @NotBlank(message = "Author name is required")
    private String author;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Admin ID is required")
    private Long adminId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0",
            message = "Price cannot be negative")
    private BigDecimal price;
}