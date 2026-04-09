package com.example.springaicrud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@EntityListeners(jakarta.persistence.PrePersist.class)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "mobile_no", nullable = false, unique = true)
    private String mobileNo;

    @Column(nullable = false)
    private String address;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_type")
    private String imageType;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "image_url",length = 500)
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ✅ Updated every time record is saved
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ Auto-called by JPA BEFORE INSERT
    @PrePersist
    public void beforeCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ Auto-called by JPA BEFORE UPDATE
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}