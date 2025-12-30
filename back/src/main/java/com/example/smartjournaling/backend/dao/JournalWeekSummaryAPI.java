package com.example.smartjournaling.backend.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.dto.JournalWeekSummaryDTO;
import com.example.smartjournaling.backend.model.JournalEntry;
import com.example.smartjournaling.backend.util.API;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Repository
public class JournalWeekSummaryAPI {
    private final API api = new API();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.token}")
    private String bearerToken;

    // Hugging Face’s current sentiment-analysis pipeline
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english";
;
    

        public JournalWeekSummaryDTO getSentiment(List<JournalEntry> last7days) {
        if (last7days == null || last7days.isEmpty()) {
            return new JournalWeekSummaryDTO("NO_DATA", 0.0);
        }

        int positiveCount = 0;
        int negativeCount = 0;
        double totalSignedScore = 0.0;
        int validEntries = 0;

        for (JournalEntry entry : last7days) {
            try {
                String jsonBody = "{\"inputs\":\"" + escapeJson(entry.getContent()) + "\"}";
                String response = api.post(API_URL, bearerToken, jsonBody);
                
                JsonNode root = objectMapper.readTree(response);
                JsonNode result = root.get(0).get(0);

                String label = result.get("label").asText(); // e.g., "POSITIVE" or "NEGATIVE"
                double score = result.get("score").asDouble(); // Confidence (e.g., 0.98)

                // Logic: If label is NEGATIVE, make the score negative for the average
                if (label.equalsIgnoreCase("POSITIVE")) {
                    positiveCount++;
                    totalSignedScore += score;
                } else if (label.equalsIgnoreCase("NEGATIVE")) {
                    negativeCount++;
                    totalSignedScore -= score; // Put a '-' in front of the value
                }
                validEntries++;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Determine Weekly Highlight Label
        String weeklyLabel;
        if (positiveCount > negativeCount) {
            weeklyLabel = "POSITIVE";
        } else if (negativeCount > positiveCount) {
            weeklyLabel = "NEGATIVE";
        } else {
            weeklyLabel = "TIE"; // Handle the same number of pos/neg
        }

        // Calculate Signed Average
        double avgScore = (validEntries > 0) ? (totalSignedScore / validEntries) : 0.0;

        return new JournalWeekSummaryDTO(weeklyLabel, avgScore);
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", " ");
    }
}
