package com.example.smartjournaling.backend.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.smartjournaling.backend.dao.JournalWeekSummaryAPI;
import com.example.smartjournaling.backend.dto.JournalWeekSummaryDTO;
import com.example.smartjournaling.backend.model.JournalEntry;
import com.example.smartjournaling.backend.model.JournalSentimentModel;
import com.example.smartjournaling.backend.repository.JournalSentimentRepository;
import com.example.smartjournaling.backend.repository.JournalWeekRepository;

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

    public Optional<JournalSentimentModel> readWeeklySentiment(String email) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        String weekStart = monday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Assumes JournalSentimentRepository has findByEmailAndWeekStart(String email, String weekStart)
        return JournalSentimentRepository.findByEmailAndWeekStart(email, weekStart);
    }
    // --- END NEW METHOD ---

   public JournalSentimentModel ComputeAndSaveWeeklySentiment(String email) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        String weekStart = monday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String weekEnd = sunday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 1. Fetch latest entries (including your recent edits)
        List<JournalEntry> last7Days = JournalWeekRepository.findByEmailAndDateBetweenOrderByDateAsc(email, weekStart, weekEnd);
        
        // 2. Perform fresh analysis using the updated content
        // This will recalculate the Majority Label and the Signed Average Score
        JournalWeekSummaryDTO weeklySentiment = JournalWeekSummaryAPI.getSentiment(last7Days);

        // 3. Find if a summary already exists for this week
        JournalSentimentModel entity = JournalSentimentRepository
            .findByEmailAndWeekStart(email, weekStart)
            .orElse(new JournalSentimentModel()); // Update existing if found, else create new

        // 4. Overwrite fields with the NEW re-calculated values
        entity.setEmail(email);
        entity.setWeekStart(weekStart);
        entity.setSentimentLabel(weeklySentiment.getLabel()); // Weekly Highlight
        entity.setSentimentScore(weeklySentiment.getScore()); // Signed Average

        // 5. Save the updated summary to the database
        JournalSentimentModel saved = JournalSentimentRepository.save(entity);
        writeSentimentToFile(saved);

        return saved;
    }

    // NOTE: This method is now redundant for the frontend's weekly summary page,
    // but is left here in case it's used elsewhere.
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