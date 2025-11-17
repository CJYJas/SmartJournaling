package com.example.smartjournaling.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Service;
import com.example.smartjournaling.backend.dao.*;
import com.example.smartjournaling.backend.repository.*;
import com.example.smartjournaling.backend.dto.*;
import com.example.smartjournaling.backend.model.*;
import com.example.smartjournaling.backend.repository.*;
import java.io.*;

@Service
public class JournalSentimentService {
    private final JournalWeekRepository JournalWeekRepository;
    private final JournalWeekSummaryAPI JournalWeekSummaryAPI;
    private final JournalSentimentRepository JournalSentimentRepository;

    public JournalSentimentService(
        JournalWeekRepository JournalWeekRepository,
        JournalWeekSummaryAPI JournalWeekSummaryAPI,
        JournalSentimentRepository JournalSentimentRepository) {
        this.JournalWeekRepository = JournalWeekRepository;
        this.JournalSentimentRepository = JournalSentimentRepository;
        this.JournalWeekSummaryAPI = JournalWeekSummaryAPI;
    }

    public JournalSentimentModel ComputeAndSaveWeeklySentiment(String email) {

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        String weekStart = monday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String weekEnd = sunday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<JournalModel> last7Days = JournalWeekRepository.findByEmailAndDateBetweenOrderByDateAsc(email, weekStart, weekEnd);
        System.out.println("📘 Found " + last7Days.size() + " journal entries for " + email);

         // 2️⃣ Call your AI API to get weekly sentiment
        JournalWeekSummaryDTO weeklySentiment = JournalWeekSummaryAPI.getSentiment(last7Days);

        Optional<JournalSentimentModel> existing = JournalSentimentRepository
            .findByEmailAndWeekStart(email, weekStart);

        JournalSentimentModel entity;
        if (existing.isPresent()) {
             // Update existing record
            entity = existing.get();
            entity.setSentimentLabel(weeklySentiment.getLabel());
            entity.setSentimentScore(weeklySentiment.getScore());
        } else {
            // Create new record
            entity = new JournalSentimentModel();
            entity.setEmail(email);
            entity.setWeekStart(weekStart);
            entity.setSentimentLabel(weeklySentiment.getLabel());
            entity.setSentimentScore(weeklySentiment.getScore());
        }

        JournalSentimentModel saved = JournalSentimentRepository.save(entity);

        writeSentimentToFile(saved);

        return saved;
    }

    public JournalSentimentModel getLatestWeekSentiment(String email) {
        return JournalSentimentRepository.findTopByEmailOrderByWeekStartDesc(email);
    } 

    private void writeSentimentToFile(JournalSentimentModel sentiment){
        File file = new File("JournalWeeklySentiment.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("Weekly Sentiment Summary for: " + sentiment.getEmail());
            writer.newLine();
            writer.write("Week Start: " + sentiment.getWeekStart());
            writer.newLine();
            writer.write("Sentiment Label: " + sentiment.getSentimentLabel());
            writer.newLine();
            writer.write("Sentiment Score: " + sentiment.getSentimentScore());
            writer.newLine();

            writer.write("--------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
