package com.example.springaicrud.service;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.entity.*;
import com.example.springaicrud.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository         bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final UserRepository         userRepository;

    // ==========================================
    // ✅ CREATE book
    // ==========================================
    public BookResponse createBook(BookRequest req) {

        BookCategory category =
                bookCategoryRepository
                        .findById(req.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: "
                                                + req.getCategoryId()));

        User admin = userRepository
                .findById(req.getAdminId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin not found: "
                                        + req.getAdminId()));

        Book saved = bookRepository.save(
                Book.builder()
                        .name(req.getName())
                        .author(req.getAuthor())
                        .category(category)
                        .admin(admin)
                        .price(req.getPrice())
                        // ✅ new book is always active
                        .isActive(true)
                        .build()
        );

        log.info("Book created: {}", saved.getName());
        return toResponse(saved);
    }

    // ==========================================
    // ✅ GET ALL books
    // ==========================================
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET AVAILABLE books (isActive = true)
    // ==========================================
    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findAll()
                .stream()
                .filter(b -> Boolean.TRUE
                        .equals(b.getIsActive()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BOUGHT books (isActive = false)
    // ==========================================
    public List<BookResponse> getBoughtBooks() {
        return bookRepository.findAll()
                .stream()
                .filter(b -> Boolean.FALSE
                        .equals(b.getIsActive()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BY ID
    // ==========================================
    public BookResponse getBookById(Long id) {
        return toResponse(
                bookRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Book not found: " + id)));
    }

    // ==========================================
    // ✅ GET BY CATEGORY
    // ==========================================
    public List<BookResponse> getBooksByCategory(
            Long categoryId) {
        return bookRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BY ADMIN
    // ==========================================
    public List<BookResponse> getBooksByAdmin(
            Long adminId) {
        return bookRepository
                .findByAdminId(adminId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ SEARCH BY NAME
    // ==========================================
    public List<BookResponse> searchByName(
            String name) {
        return bookRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ SEARCH BY AUTHOR
    // ==========================================
    public List<BookResponse> searchByAuthor(
            String author) {
        return bookRepository
                .findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ UPDATE book
    // ==========================================
    public BookResponse updateBook(
            Long id, BookRequest req) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found: " + id));

        BookCategory category =
                bookCategoryRepository
                        .findById(req.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: "
                                                + req.getCategoryId()));

        User admin = userRepository
                .findById(req.getAdminId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin not found: "
                                        + req.getAdminId()));

        book.setName(req.getName());
        book.setAuthor(req.getAuthor());
        book.setCategory(category);
        book.setAdmin(admin);
        book.setPrice(req.getPrice());

        return toResponse(bookRepository.save(book));
    }

    // ==========================================
    // ✅ DELETE book
    // ==========================================
    public String deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException(
                    "Book not found: " + id);
        }
        bookRepository.deleteById(id);
        return "Book deleted successfully!";
    }

    // ==========================================
    // 🔧 Convert Entity → Response
    // ==========================================
    public BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .price(book.getPrice())
                .isActive(book.getIsActive())
                // ✅ human readable status
                .status(Boolean.TRUE.equals(
                        book.getIsActive())
                        ? "AVAILABLE" : "BOUGHT")
                .categoryId(
                        book.getCategory().getId())
                .categoryName(
                        book.getCategory().getCategory())
                .adminId(
                        book.getAdmin().getId())
                .adminName(
                        book.getAdmin().getName())
                .adminEmail(
                        book.getAdmin().getEmail())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}