package com.example.springaicrud.repository;

import com.example.springaicrud.entity.BookCategory;
import org.springframework.data.jpa.repository
        .JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookCategoryRepository
        extends JpaRepository<BookCategory, Long> {

    // find by category name
    Optional<BookCategory> findByCategory(String category);

    // check if category exists
    boolean existsByCategory(String category);
}