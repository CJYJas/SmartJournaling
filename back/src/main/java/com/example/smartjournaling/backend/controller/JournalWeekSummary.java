package com.example.smartjournaling.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.smartjournaling.backend.model.*;
import com.example.smartjournaling.backend.service.JournalSentimentService;

import java.util.Map;

@RestController
@RequestMapping("/journal")
public class JournalWeekSummary {

    @Autowired
    private JournalSentimentService journalSentimentService;

    // Compute and save weekly sentiment for user (email from request body)
    @PostMapping("/weekly-sentiment")
    public JournalSentimentModel computeAndSaveWeeklySentiment(@RequestBody Map<String, String> request) {
        String email = request.get("email"); // get email from request body
        System.out.println("📩 Controller received request for user = " + email);
        return journalSentimentService.ComputeAndSaveWeeklySentiment(email);
    }

    // Get weekly sentiment for current week (email from query parameter)
    @GetMapping("/weekly-sentiment")
    public ResponseEntity<JournalSentimentModel> getWeeklySentiment(@RequestParam String email) {
        return journalSentimentService.readWeeklySentiment(email)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

