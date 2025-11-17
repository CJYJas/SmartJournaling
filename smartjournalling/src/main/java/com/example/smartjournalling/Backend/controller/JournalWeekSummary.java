package com.example.smartjournalling.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.smartjournalling.Backend.model.*;
import com.example.smartjournalling.Backend.service.JournalSentimentService;

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

    // Get latest weekly sentiment for user (email from query parameter)
    @GetMapping("/weekly-sentiment")
    public JournalSentimentModel getLatestWeekSentiment(@RequestParam String email) {
        return journalSentimentService.getLatestWeekSentiment(email);
    }
}

