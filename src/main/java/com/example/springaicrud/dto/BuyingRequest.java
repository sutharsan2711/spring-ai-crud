package com.example.springaicrud.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// ✅ @Data generates all getters/setters
@Data
public class BuyingRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;       // ✅ getBookId()

    @NotNull(message = "User ID is required")
    private Long userId;       // ✅ getUserId()

    // ✅ borrowDays field was missing
    private Integer borrowDays; // ✅ getBorrowDays()
}