package com.example.springaicrud.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    // ✅ Date info
    private LocalDate  issueDate;
    private LocalDate  dueDate;
    private LocalDate  returnDate;

    // ✅ Fine info
    private BigDecimal fineAmount;
    private Boolean    finePaid;
    private Integer    daysLate;

    // ✅ Status
    private String     status;
    private String     message;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}