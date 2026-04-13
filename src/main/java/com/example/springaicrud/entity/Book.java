package com.example.springaicrud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Getter         // ✅ getName(), getIsActive()
@Setter         // ✅ setIsActive()
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

        @Id
        @GeneratedValue(strategy =
                GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private String author;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "category_id",
                nullable = false)
        private BookCategory category;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "admin_id",
                nullable = false)
        private User admin;

        @Column(nullable = false,
                precision = 10, scale = 2)
        private BigDecimal price;

        @Column(name = "is_active",
                nullable = false)
        private Boolean isActive;

        @Column(name = "created_at",
                updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @PrePersist
        public void beforeCreate() {
                LocalDateTime now = LocalDateTime.now();
                this.createdAt = now;
                this.updatedAt = now;
                if (this.isActive == null) {
                        this.isActive = true;
                }
        }

        @PreUpdate
        public void beforeUpdate() {
                this.updatedAt = LocalDateTime.now();
        }
}