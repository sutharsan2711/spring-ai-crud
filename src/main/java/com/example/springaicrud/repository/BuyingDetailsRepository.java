package com.example.springaicrud.repository;

import com.example.springaicrud.entity.BuyingDetails;
import org.springframework.data.jpa.repository
        .JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyingDetailsRepository
        extends JpaRepository<BuyingDetails, Long> {

    // get all purchases by user
    List<BuyingDetails> findByUserId(Long userId);

    // ✅ FIXED: Only ONE findByBookId
    // Returns List (removed the duplicate Optional one)
    List<BuyingDetails> findByBookId(Long bookId);

    // check if user already bought this book
    boolean existsByBookIdAndUserId(
            Long bookId, Long userId);

    // ✅ NEW: find latest purchase by bookId and userId
    Optional<BuyingDetails> findByBookIdAndUserId(
            Long bookId, Long userId);
    List<BuyingDetails> findByStatus(String status);

    // ✅ NEW: find unpaid fines
    List<BuyingDetails> findByFinePaidFalse();
}