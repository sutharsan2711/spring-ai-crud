package com.example.springaicrud.repository;

import com.example.springaicrud.entity.Book;
import org.springframework.data.jpa.repository
        .JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository
        extends JpaRepository<Book, Long> {

    // get all books by category
    List<Book> findByCategoryId(Long categoryId);

    // get all books by admin
    List<Book> findByAdminId(Long adminId);

    // search books by name
    List<Book> findByNameContainingIgnoreCase(
            String name);

    // search books by author
    List<Book> findByAuthorContainingIgnoreCase(
            String author);
}