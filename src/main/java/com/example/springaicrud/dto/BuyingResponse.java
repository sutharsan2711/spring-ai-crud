package com.example.springaicrud.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyingResponse {
    private Long       id;

    // book info
    private Long       bookId;
    private String     bookName;
    private String     bookAuthor;
    private BigDecimal bookPrice;

    // user info
    private Long       userId;
    private String     userName;
    private String     userEmail;

    private String     message;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}