package com.example.smartjournaling.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RestController;

import com.example.smartjournaling.backend.model.SentimentAnalysisModel;
import com.example.smartjournaling.backend.service.SentimentAnalysisService;

@RestController
@RequestMapping("/journal")
public class SentimentAnalysisController {

    private final SentimentAnalysisService sentimentAnalysisService;

    public SentimentAnalysisController(SentimentAnalysisService sentimentAnalysisService) {
    this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @GetMapping("/sentiment") 
    public ResponseEntity<SentimentAnalysisModel> readSentiment(
    @RequestParam String email, 
    @RequestParam String date) 
    {
    return sentimentAnalysisService.readSentiment(email, date)
    .map(ResponseEntity::ok) // If found, return 200 OK with the model
    .orElseGet(() -> ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

   @PostMapping("/analyze")
    public SentimentAnalysisModel getSentiment(@RequestBody Map<String, String> request) {
        String email = request.get("email");   // get email from request body
        String date = request.get("date");     // get date from request body
        return sentimentAnalysisService.computeAndSaveSentiment(email, date);
    }
    }