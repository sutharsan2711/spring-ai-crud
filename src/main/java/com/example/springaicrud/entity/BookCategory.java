package com.example.springaicrud.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // category name must be unique
    // e.g. Fiction, Science, History
    @Column(nullable = false, unique = true)
    private String category;
}