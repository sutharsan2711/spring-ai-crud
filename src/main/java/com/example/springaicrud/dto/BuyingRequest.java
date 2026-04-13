package com.example.springaicrud.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter          // ✅ getBookId(), getUserId()
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyingRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private Integer borrowDays;
}