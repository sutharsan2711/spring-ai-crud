package com.example.springaicrud.service;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.entity.*;
import com.example.springaicrud.repository.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory
        .annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction
        .annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final EmailService
            emailService;

    // fine per day from properties
    @Value("${fine.per.day:10.00}")
    private BigDecimal finePerDay;

    // default borrow days
    @Value("${fine.due.days:14}")
    private Integer defaultDueDays;

    // ==========================================
// ✅ BUY a book — FIXED
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

        // check book available
        if (Boolean.FALSE.equals(book.getIsActive())) {
            throw new RuntimeException(
                    "Book already bought: "
                            + book.getName());
        }

        // find user
        User user = userRepository
                .findById(req.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: "
                                        + req.getUserId()));

        // check already bought
        if (buyingDetailsRepository
                .existsByBookIdAndUserId(
                        req.getBookId(),
                        req.getUserId())) {
            throw new RuntimeException(
                    "User already bought this book!");
        }

        // ✅ Calculate dates safely
        LocalDate issueDate = LocalDate.now();

        // ✅ Use borrowDays if provided
        // otherwise use default 14 days
        int borrowDays = (req.getBorrowDays() != null
                && req.getBorrowDays() > 0)
                ? req.getBorrowDays()
                : (defaultDueDays != null
                   ? defaultDueDays : 14);

        LocalDate dueDate = issueDate
                .plusDays(borrowDays);

        // update book status
        book.setIsActive(false);
        bookRepository.save(book);

        // save buying record
        BuyingDetails saved =
                buyingDetailsRepository.save(
                        BuyingDetails.builder()
                                .book(book)
                                .user(user)
                                .issueDate(issueDate)   // ✅ always set
                                .dueDate(dueDate)       // ✅ always set
                                .fineAmount(BigDecimal.ZERO)
                                .finePaid(false)
                                .daysLate(0)
                                .status("ACTIVE")
                                .build()
                );

        // send email safely
        try {
            emailService.sendBookIssuedEmail(saved);
        } catch (Exception e) {
            log.warn("Email failed but buy success: {}",
                    e.getMessage());
        }

        log.info("Book '{}' bought by '{}'",
                book.getName(), user.getName());

        return toResponse(saved,
                "Book bought successfully! Due: "
                        + dueDate);
    }

    // ==========================================
    // ✅ RETURN a book
    // ==========================================
    @Transactional
    public BuyingResponse returnBook(
            BuyingRequest req) {

        // find book
        Book book = bookRepository
                .findById(req.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found: "
                                        + req.getBookId()));

        // check book is bought
        if (Boolean.TRUE
                .equals(book.getIsActive())) {
            throw new RuntimeException(
                    "Book is not bought: "
                            + book.getName());
        }

        // check user bought this book
        if (!buyingDetailsRepository
                .existsByBookIdAndUserId(
                        req.getBookId(),
                        req.getUserId())) {
            throw new RuntimeException(
                    "This user did not buy "
                            + "this book!");
        }

        // find buying record
        BuyingDetails buying =
                buyingDetailsRepository
                        .findByBookIdAndUserId(
                                req.getBookId(),
                                req.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Buying record not found!"));

        // ✅ Set return date
        LocalDate returnDate = LocalDate.now();
        buying.setReturnDate(returnDate);

        // ✅ Calculate fine
        // fine = days_late * fine_per_day
        long daysLate = 0;
        BigDecimal fineAmount = BigDecimal.ZERO;

        if (returnDate.isAfter(
                buying.getDueDate())) {
            daysLate = ChronoUnit.DAYS.between(
                    buying.getDueDate(), returnDate);
            fineAmount = finePerDay.multiply(
                    BigDecimal.valueOf(daysLate));
        }

        buying.setDaysLate((int) daysLate);
        buying.setFineAmount(fineAmount);
        buying.setStatus(
                daysLate > 0 ? "RETURNED_LATE"
                        : "RETURNED_ONTIME");

        // ✅ Update book isActive → TRUE
        book.setIsActive(true);
        bookRepository.save(book);

        // Save updated buying record
        BuyingDetails updated =
                buyingDetailsRepository.save(buying);

        // ✅ Send return email
        emailService.sendBookReturnedEmail(
                updated);

        String message = daysLate > 0
                ? "Book returned late! "
                  + "Fine: ₹" + fineAmount
                  + " for " + daysLate + " days"
                : "Book returned on time! "
                  + "No fine. Thank you!";

        log.info("Book '{}' returned. "
                        + "Days late: {}, Fine: ₹{}",
                book.getName(),
                daysLate, fineAmount);

        return toResponse(updated, message);
    }

    // ==========================================
    // ✅ PAY FINE
    // ==========================================
    @Transactional
    public BuyingResponse payFine(Long id) {

        BuyingDetails buying =
                buyingDetailsRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Record not found: " + id));

        if (buying.getFineAmount()
                .compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException(
                    "No fine to pay!");
        }

        if (Boolean.TRUE
                .equals(buying.getFinePaid())) {
            throw new RuntimeException(
                    "Fine already paid!");
        }

        buying.setFinePaid(true);
        BuyingDetails updated =
                buyingDetailsRepository.save(buying);

        log.info("Fine paid for record: {}", id);

        return toResponse(updated,
                "Fine of ₹" + buying.getFineAmount()
                        + " paid successfully!");
    }

    // ==========================================
    // ✅ GET ALL
    // ==========================================
    public List<BuyingResponse> getAllBuyingDetails() {
        return buyingDetailsRepository.findAll()
                .stream()
                .map(b -> toResponse(b, ""))
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET OVERDUE records
    // ==========================================
    public List<BuyingResponse> getOverdueBooks() {
        LocalDate today = LocalDate.now();
        return buyingDetailsRepository.findAll()
                .stream()
                .filter(b ->
                        b.getDueDate() != null
                                && today.isAfter(b.getDueDate())
                                && "ACTIVE".equals(b.getStatus()))
                .map(b -> toResponse(b, "OVERDUE"))
                .collect(Collectors.toList());
    }

    // ==========================================
    // ✅ GET BY USER
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
    // ✅ GET BY BOOK
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
    public BuyingResponse toResponse(
            BuyingDetails buying,
            String message) {

        // ✅ Calculate current fine
        // if still active and overdue
        BigDecimal currentFine =
                buying.getFineAmount() != null
                        ? buying.getFineAmount()
                        : BigDecimal.ZERO;

        if ("ACTIVE".equals(buying.getStatus())
                && buying.getDueDate() != null
                && LocalDate.now().isAfter(
                buying.getDueDate())) {

            long daysLate = ChronoUnit.DAYS.between(
                    buying.getDueDate(),
                    LocalDate.now());

            currentFine = finePerDay != null
                    ? finePerDay.multiply(
                    BigDecimal.valueOf(daysLate))
                    : BigDecimal.valueOf(daysLate * 10);
        }

        return BuyingResponse.builder()
                .id(buying.getId())
                .bookId(buying.getBook().getId())
                .bookName(buying.getBook().getName())
                .bookAuthor(buying.getBook().getAuthor())
                .bookPrice(buying.getBook().getPrice())
                .userId(buying.getUser().getId())
                .userName(buying.getUser().getName())
                .userEmail(buying.getUser().getEmail())
                // ✅ All date fields safe
                .issueDate(buying.getIssueDate())
                .dueDate(buying.getDueDate())
                .returnDate(buying.getReturnDate())
                .fineAmount(currentFine)
                .finePaid(buying.getFinePaid() != null
                        ? buying.getFinePaid() : false)
                .daysLate(buying.getDaysLate() != null
                        ? buying.getDaysLate() : 0)
                .status(buying.getStatus() != null
                        ? buying.getStatus() : "ACTIVE")
                .message(message)
                .createdAt(buying.getCreatedAt())
                .updatedAt(buying.getUpdatedAt())
                .build();
    }
}