package com.example.springaicrud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "buying_details")
// ✅ All these are required
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder           // ✅ generates builder()
public class BuyingDetails {

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id",
            nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "fine_amount",
            precision = 10, scale = 2)
    private BigDecimal fineAmount;

    @Column(name = "fine_paid")
    private Boolean finePaid;

    @Column(name = "days_late")
    private Integer daysLate;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt  = now;
        this.updatedAt  = now;
        if (this.finePaid == null)
            this.finePaid = false;
        if (this.fineAmount == null)
            this.fineAmount = BigDecimal.ZERO;
        if (this.daysLate == null)
            this.daysLate = 0;
        if (this.status == null)
            this.status = "ACTIVE";
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}