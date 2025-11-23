package com.example.smartjournaling.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.smartjournaling.backend.model.*;
import com.example.smartjournaling.backend.service.SentimentAnalysisService;

import java.util.Map;

@RestController
@RequestMapping("/journal")
public class SentimentAnalysisController {

    private final SentimentAnalysisService sentimentAnalysisService;

    public SentimentAnalysisController(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
    }
    
    @PostMapping("/analyze")
    public SentimentAnalysisModel getSentiment(@RequestBody Map<String, String> request) {
        String email = request.get("email");   // get email from request body
        String date = request.get("date");     // get date from request body
        return sentimentAnalysisService.getSentiment(email, date);
    }
}

