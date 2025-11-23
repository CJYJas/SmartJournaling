package com.example.smartjournaling.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.smartjournaling.backend.model.*;
import com.example.smartjournaling.backend.dto.*;
import com.example.smartjournaling.backend.model.*;
import com.example.smartjournaling.backend.dao.*;
import com.example.smartjournaling.backend.repository.*;

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

    public SentimentAnalysisModel getSentiment (String email, String date) {
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
        entity.setSentimentlabel(sentiment. getSentimentlabel());
        entity.setSentimentscore(sentiment.getSentimentscore());

        return SentimentAnalysisRepository.save(entity);
    }
 
}
