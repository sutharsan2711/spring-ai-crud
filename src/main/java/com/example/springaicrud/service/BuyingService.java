package com.example.springaicrud.service;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.entity.*;
import com.example.springaicrud.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuyingService {

    private final BuyingDetailsRepository
            buyingDetailsRepository;
    private final BookRepository
            bookRepository;
    private final UserRepository
            userRepository;

    // ==========================================
    // ✅ BUY a book
    // book isActive: TRUE → FALSE
    // ==========================================
    @Transactional
    public BuyingResponse buyBook(BuyingRequest req) {

        // find book
        Book book = bookRepository
                .findById(req.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found: "
                                        + req.getBookId()));

        // ✅ check book is available
        if (Boolean.FALSE.equals(book.getIsActive())) {
            throw new RuntimeException(
                    "Book is already bought "
                            + "and not available: "
                            + book.getName());
        }

        // find user
        User user = userRepository
                .findById(req.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: "
                                        + req.getUserId()));

        // ✅ check user already bought this book
        if (buyingDetailsRepository
                .existsByBookIdAndUserId(
                        req.getBookId(),
                        req.getUserId())) {
            throw new RuntimeException(
                    "User already bought this book!");
        }

        // ✅ Update book isActive → FALSE (bought)
        book.setIsActive(false);
        bookRepository.save(book);

        log.info("Book '{}' bought by '{}'",
                book.getName(),
                user.getName());

        // ✅ Save buying details
        BuyingDetails saved =
                buyingDetailsRepository.save(
                        BuyingDetails.builder()
                                .book(book)
                                .user(user)
                                .build()
                );

        return toResponse(saved,
                "Book bought successfully!");
    }

    // ==========================================
    // ✅ RETURN a book
    // book isActive: FALSE → TRUE
    // ==========================================
    // ==========================================
// ✅ RETURN a book
// ==========================================
    @Transactional
    public BuyingResponse returnBook(BuyingRequest req) {

        // find book
        Book book = bookRepository
                .findById(req.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found: "
                                        + req.getBookId()));

        // check book is actually bought
        if (Boolean.TRUE.equals(book.getIsActive())) {
            throw new RuntimeException(
                    "Book is not bought yet: "
                            + book.getName());
        }

        // find user
        User user = userRepository
                .findById(req.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: "
                                        + req.getUserId()));

        // check this user bought the book
        if (!buyingDetailsRepository
                .existsByBookIdAndUserId(
                        req.getBookId(),
                        req.getUserId())) {
            throw new RuntimeException(
                    "This user did not buy this book!");
        }

        // ✅ Update book isActive → TRUE (available)
        book.setIsActive(true);
        bookRepository.save(book);

        log.info("Book '{}' returned by '{}'",
                book.getName(),
                user.getName());

        // ✅ FIXED: use findByBookIdAndUserId
        // instead of findByBookId
        BuyingDetails buying =
                buyingDetailsRepository
                        .findByBookIdAndUserId(
                                req.getBookId(),
                                req.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Buying record not found!"));

        // update timestamp
        buyingDetailsRepository.save(buying);

        return toResponse(buying,
                "Book returned successfully! "
                        + "Book is now available.");
    }

    // ==========================================
    // ✅ GET ALL buying details
    // ==========================================
    public List<BuyingResponse> getAllBuyingDetails() {
        return buyingDetailsRepository.findAll()
                .stream()
                .map(b -> toResponse(b, ""))
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BY USER — what user bought
    // ==========================================
    public List<BuyingResponse> getByUser(
            Long userId) {
        return buyingDetailsRepository
                .findByUserId(userId)
                .stream()
                .map(b -> toResponse(b, ""))
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BY BOOK — who bought the book
    // ==========================================
    public List<BuyingResponse> getByBook(
            Long bookId) {
        return buyingDetailsRepository
                .findByBookId(bookId)
                .stream()
                .map(b -> toResponse(b, ""))
                .collect(Collectors.toList());
    }

    // ==========================================
    // 🔧 Convert Entity → Response
    // ==========================================
    private BuyingResponse toResponse(
            BuyingDetails buying, String message) {
        return BuyingResponse.builder()
                .id(buying.getId())
                .bookId(buying.getBook().getId())
                .bookName(buying.getBook().getName())
                .bookAuthor(
                        buying.getBook().getAuthor())
                .bookPrice(
                        buying.getBook().getPrice())
                .userId(buying.getUser().getId())
                .userName(buying.getUser().getName())
                .userEmail(
                        buying.getUser().getEmail())
                .message(message)
                .createdAt(buying.getCreatedAt())
                .updatedAt(buying.getUpdatedAt())
                .build();
    }
}