package com.example.smartjournaling.backend.service;

import java.util.List;
import java.util.Optional; // Need this for clean Optional handling
import org.springframework.stereotype.Service;
import com.example.smartjournaling.backend.model.*;
import com.example.smartjournaling.backend.dto.*;
import com.example.smartjournaling.backend.repository.*;
import com.example.smartjournaling.backend.dao.SentimentAnalysisDAO;

@Service
public class SentimentAnalysisService {
    private final SentimentAnalysisDAO SentimentAnalysisDAO;
    private final SentimentAnalysisRepository SentimentAnalysisRepository;
    private final JournalWeekRepository JournalWeekRepository;

    public SentimentAnalysisService(
        SentimentAnalysisDAO SentimentAnalysisAPI,
        SentimentAnalysisRepository SentimentRepository,
        JournalWeekRepository JournalRepository) {
        this.SentimentAnalysisDAO = SentimentAnalysisAPI;
        this.SentimentAnalysisRepository = SentimentRepository;
        this.JournalWeekRepository = JournalRepository;
    }

    /**
     * 1. READ EXISTING SENTIMENT (Non-creating operation)
     * This will be called by the new @GetMapping in the Controller.
     */
    public Optional<SentimentAnalysisModel> readSentiment(String email, String date) {
        // ASSUMPTION: You have a method in your Repository to find by email and date.
        // If not, you must add it (e.g., findByEmailAndDate).
        return SentimentAnalysisRepository.findByEmailAndDate(email, date);
    }

    /**
     * 2. COMPUTE AND SAVE SENTIMENT (Creating/Writing operation)
     * This will be called by the @PostMapping in the Controller and executes only if
     * the frontend couldn't read the data.
     */
    public SentimentAnalysisModel computeAndSaveSentiment(String email, String date) {
        List<JournalEntry> todaysEntries = JournalWeekRepository.findByEmailAndDate(email, date);

        if (todaysEntries.isEmpty()) {
            throw new RuntimeException("No journal entry found for user " + email + " on " + date);
        }

        // Assuming only one entry per user per date:
        JournalEntry entry = todaysEntries.get(0);

        SentimentAnalysisDTO sentiment = SentimentAnalysisDAO.analyzeSentiment(entry.getContent());

        SentimentAnalysisModel entity = new SentimentAnalysisModel();
        entity.setEmail(email);
        entity.setDate(date);
        entity.setSentimentlabel(sentiment.getSentimentlabel());
        entity.setSentimentscore(sentiment.getSentimentscore());

        // This line is now correctly placed inside the Compute/Save method.
        return SentimentAnalysisRepository.save(entity);
    }
}