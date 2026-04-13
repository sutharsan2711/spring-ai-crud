package com.example.springaicrud.service;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.entity.BookCategory;
import com.example.springaicrud.repository
        .BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class BookCategoryService {

    private final BookCategoryRepository
            bookCategoryRepository;

    // ==========================================
    // ✅ CREATE category
    // ==========================================
    public BookCategoryResponse createCategory(
            BookCategoryRequest req) {

        // check category already exists
        if (bookCategoryRepository
                .existsByCategory(req.getCategory())) {
            throw new RuntimeException(
                    "Category already exists: "
                            + req.getCategory());
        }

        BookCategory saved =
                bookCategoryRepository.save(
                        BookCategory.builder()
                                .category(req.getCategory())
                                .build()
                );

        log.info("Category created: {}",
                saved.getCategory());

        return toResponse(saved);
    }

    // ==========================================
    // ✅ GET ALL categories
    // ==========================================
    public List<BookCategoryResponse> getAllCategories() {
        return bookCategoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BY ID
    // ==========================================
    public BookCategoryResponse getCategoryById(
            Long id) {
        BookCategory cat =
                bookCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: " + id));
        return toResponse(cat);
    }

    // ==========================================
    // ✅ UPDATE category
    // ==========================================
    public BookCategoryResponse updateCategory(
            Long id, BookCategoryRequest req) {

        BookCategory cat =
                bookCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: " + id));

        // check new name conflicts
        if (bookCategoryRepository
                .existsByCategory(req.getCategory())
                && !cat.getCategory()
                .equals(req.getCategory())) {
            throw new RuntimeException(
                    "Category name already exists: "
                            + req.getCategory());
        }

        cat.setCategory(req.getCategory());
        BookCategory updated =
                bookCategoryRepository.save(cat);

        log.info("Category updated: {}",
                updated.getCategory());

        return toResponse(updated);
    }

    // ==========================================
    // ✅ DELETE category
    // ==========================================
    public String deleteCategory(Long id) {
        if (!bookCategoryRepository.existsById(id)) {
            throw new RuntimeException(
                    "Category not found: " + id);
        }
        bookCategoryRepository.deleteById(id);
        return "Category deleted successfully!";
    }

    // ==========================================
    // 🔧 Convert Entity → Response DTO
    // ==========================================
    private BookCategoryResponse toResponse(
            BookCategory cat) {
        return BookCategoryResponse.builder()
                .id(cat.getId())
                .category(cat.getCategory())
                .build();
    }
}