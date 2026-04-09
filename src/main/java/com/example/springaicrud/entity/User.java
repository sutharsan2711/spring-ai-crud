package com.example.springaicrud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // unique email
    @Column(nullable = false, unique = true)
    private String email;

    // stored as BCrypt hash
    @Column(nullable = false)
    private String password;

    // USER or ADMIN
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name       = "role_id",
            nullable   = false
    )
    private Role role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt    = now;
        this.updatedAt    = now;

    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}