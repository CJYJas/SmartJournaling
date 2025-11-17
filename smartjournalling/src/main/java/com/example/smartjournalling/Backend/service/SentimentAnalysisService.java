package com.example.smartjournalling.Backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.smartjournalling.Backend.model.*;
import com.example.smartjournalling.Backend.dto.*;
import com.example.smartjournalling.Backend.model.*;
import com.example.smartjournalling.Backend.dao.*;
import com.example.smartjournalling.Backend.repository.*;

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
        List<JournalModel> todaysEntries = JournalWeekRepository.findByEmailAndDate(email, date);

        if (todaysEntries.isEmpty()) {
            throw new RuntimeException("No journal entry found for user " + email + " on " + date);
        }

        // Assuming only one entry per user per date:
        JournalModel entry = todaysEntries.get(0);

        SentimentAnalysisDTO sentiment = SentimentAnalysisDAO.analyzeSentiment(entry.getContent());

        SentimentAnalysisModel entity = new SentimentAnalysisModel();
        entity.setEmail(email);
        entity.setDate(date);
        entity.setSentimentlabel(sentiment. getSentimentlabel());
        entity.setSentimentscore(sentiment.getSentimentscore());

        return SentimentAnalysisRepository.save(entity);
    }
 
}
