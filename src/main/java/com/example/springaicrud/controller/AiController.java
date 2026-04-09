package com.example.springaicrud.controller;

import com.example.springaicrud.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        return ResponseEntity.ok(aiService.askAi(question));
    }

    @GetMapping("/bio")
    public ResponseEntity<String> generateBio(
            @RequestParam String name,
            @RequestParam String address) {
        return ResponseEntity.ok(aiService.generatePersonSummary(name, address));
    }
}