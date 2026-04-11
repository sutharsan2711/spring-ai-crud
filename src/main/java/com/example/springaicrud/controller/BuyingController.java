package com.example.springaicrud.controller;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.service
        .BuyingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buying")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuyingController {

    private final BuyingService buyingService;

    // ✅ BUY a book
    // POST /api/buying/buy
    @PostMapping("/buy")
    public ResponseEntity<BuyingResponse> buy(
            @Valid @RequestBody BuyingRequest req) {
        return ResponseEntity.ok(
                buyingService.buyBook(req));
    }

    // ✅ RETURN a book
    // POST /api/buying/return
    @PostMapping("/return")
    public ResponseEntity<BuyingResponse> returnBook(
            @Valid @RequestBody BuyingRequest req) {
        return ResponseEntity.ok(
                buyingService.returnBook(req));
    }

    // ✅ GET ALL buying details
    // GET /api/buying
    @GetMapping
    public ResponseEntity<List<BuyingResponse>>
    getAll() {
        return ResponseEntity.ok(
                buyingService.getAllBuyingDetails());
    }

    // ✅ GET by USER
    // GET /api/buying/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BuyingResponse>>
    getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                buyingService.getByUser(userId));
    }

    // ✅ GET by BOOK
    // GET /api/buying/book/1
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BuyingResponse>>
    getByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(
                buyingService.getByBook(bookId));
    }
    // ✅ PAY FINE
    @PostMapping("/pay-fine/{id}")
    public ResponseEntity<BuyingResponse> payFine(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                buyingService.payFine(id));
    }

}