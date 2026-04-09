package com.example.springaicrud.controller;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.service
        .BookCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookCategoryController {

    private final BookCategoryService
            bookCategoryService;

    // ✅ CREATE
    // POST /api/categories
    @PostMapping
    public ResponseEntity<BookCategoryResponse>
    create(
            @Valid @RequestBody
            BookCategoryRequest req) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookCategoryService
                        .createCategory(req));
    }

    // ✅ GET ALL
    // GET /api/categories
    @GetMapping
    public ResponseEntity<List<BookCategoryResponse>>
    getAll() {
        return ResponseEntity.ok(
                bookCategoryService.getAllCategories());
    }

    // ✅ GET BY ID
    // GET /api/categories/1
    @GetMapping("/{id}")
    public ResponseEntity<BookCategoryResponse>
    getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                bookCategoryService.getCategoryById(id));
    }

    // ✅ UPDATE
    // PUT /api/categories/1
    @PutMapping("/{id}")
    public ResponseEntity<BookCategoryResponse>
    update(
            @PathVariable Long id,
            @Valid @RequestBody
            BookCategoryRequest req) {
        return ResponseEntity.ok(
                bookCategoryService
                        .updateCategory(id, req));
    }

    // ✅ DELETE
    // DELETE /api/categories/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                bookCategoryService.deleteCategory(id));
    }
}