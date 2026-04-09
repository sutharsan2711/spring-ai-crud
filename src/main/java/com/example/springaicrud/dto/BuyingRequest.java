package com.example.springaicrud.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuyingRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "User ID is required")
    private Long userId;
}