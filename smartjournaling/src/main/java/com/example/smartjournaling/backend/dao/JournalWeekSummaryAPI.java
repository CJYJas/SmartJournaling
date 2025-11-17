package com.example.smartjournaling.backend.dao;

import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.dto.JournalWeekSummaryDTO;
import com.example.smartjournaling.backend.model.JournalModel;
import com.example.smartjournaling.backend.util.API;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class JournalWeekSummaryAPI {
    private final API api = new API();
    private final ObjectMapper objectMapper = new ObjectMapper();

     @Value("${huggingface.token}")
    private String bearerToken;
    // Hugging Face’s current sentiment-analysis pipeline
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english";
;
    

    public JournalWeekSummaryDTO  getSentiment(List<JournalModel> last7days){
        if (last7days == null || last7days.isEmpty()) {
            return new JournalWeekSummaryDTO("NO_DATA", 0.0);
        }

        List<JournalWeekSummaryDTO> dailyResults = new ArrayList<>();
        System.out.println("🟢 Starting sentiment analysis for last 7 days...");
        for (JournalModel entry : last7days) {
            try{
                //Request body

                String jsonBody = "{\"inputs\":\"" + escapeJson(entry.getContent()) + "\"}";
                System.out.println("Sending to API: " + jsonBody);

                //Make API call
                String response = api.post(API_URL, bearerToken, jsonBody);
                System.out.println("API response: " + response);

                //Parse response
                JsonNode root = objectMapper.readTree(response);
                JsonNode result = root.get(0).get(0);

                String label = result.get("label").asText();
                double score = result.get("score").asDouble();

                dailyResults.add(new JournalWeekSummaryDTO(label, score));
            } catch (Exception e) {
                e.printStackTrace();
                dailyResults.add(new JournalWeekSummaryDTO("ERROR", 0.0));
            }
        }

        double avgScore = dailyResults.stream()
                .filter(dto -> !dto.getLabel().equals("ERROR"))
                .mapToDouble(JournalWeekSummaryDTO::getScore)
                .average()
                .orElse(0.0);

        String majorityLabel = dailyResults.stream()
                .collect(Collectors.groupingBy(JournalWeekSummaryDTO::getLabel, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NO_DATA");

        return new JournalWeekSummaryDTO(majorityLabel, avgScore);
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", " ");
    }
}
