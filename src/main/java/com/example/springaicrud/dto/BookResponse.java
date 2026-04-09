package com.example.springaicrud.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder                    // ✅ Builder needed for .price()
public class BookResponse {

    private Long       id;
    private String     name;
    private String     author;

    // ✅ THIS WAS MISSING — add price field
    private BigDecimal price;

    // ✅ THIS WAS MISSING — add isActive field
    private Boolean    isActive;

    // ✅ THIS WAS MISSING — AVAILABLE or BOUGHT
    private String     status;

    private Long       categoryId;
    private String     categoryName;

    private Long       adminId;
    private String     adminName;
    private String     adminEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}