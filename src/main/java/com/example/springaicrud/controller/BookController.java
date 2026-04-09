package com.example.springaicrud.controller;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest req) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.createBook(req));
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<BookResponse>>
    getAll() {
        return ResponseEntity.ok(
                bookService.getAllBooks());
    }

    // ✅ GET AVAILABLE books
    @GetMapping("/available")
    public ResponseEntity<List<BookResponse>>
    getAvailable() {
        return ResponseEntity.ok(
                bookService.getAvailableBooks());
    }

    // ✅ GET BOUGHT books
    @GetMapping("/bought")
    public ResponseEntity<List<BookResponse>>
    getBought() {
        return ResponseEntity.ok(
                bookService.getBoughtBooks());
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                bookService.getBookById(id));
    }

    // ✅ GET BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookResponse>>
    getByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                bookService.getBooksByCategory(
                        categoryId));
    }

    // ✅ GET BY ADMIN
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<BookResponse>>
    getByAdmin(
            @PathVariable Long adminId) {
        return ResponseEntity.ok(
                bookService.getBooksByAdmin(adminId));
    }

    // ✅ SEARCH BY NAME
    @GetMapping("/search/name")
    public ResponseEntity<List<BookResponse>>
    searchByName(@RequestParam String q) {
        return ResponseEntity.ok(
                bookService.searchByName(q));
    }

    // ✅ SEARCH BY AUTHOR
    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponse>>
    searchByAuthor(@RequestParam String q) {
        return ResponseEntity.ok(
                bookService.searchByAuthor(q));
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest req) {
        return ResponseEntity.ok(
                bookService.updateBook(id, req));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                bookService.deleteBook(id));
    }
}