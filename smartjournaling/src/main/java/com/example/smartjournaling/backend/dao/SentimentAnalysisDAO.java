package com.example.smartjournaling.backend.dao;

import org.springframework.stereotype.Repository;
import com.example.smartjournaling.backend.util.API;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.example.smartjournaling.backend.dto.SentimentAnalysisDTO;


@Repository
public class SentimentAnalysisDAO {
    private final API api = new API();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.token}")
    private String bearerToken;

    // Hugging Face’s current sentiment-analysis pipeline
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english";

    public SentimentAnalysisDTO analyzeSentiment(String content) {
        try{
            //Request body
            String jsonBody = objectMapper.writeValueAsString(content);
            jsonBody = "{\"inputs\":" + jsonBody + "}";
            System.out.println("Sending to API: " + jsonBody);

            //Make API call
            String response = api.post(API_URL, bearerToken, jsonBody);
            System.out.println("API response: " + response);

            //Parse response                
            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.get(0).get(0);

            String label = result.get("label").asText();
            double score = result.get("score").asDouble();

            return new SentimentAnalysisDTO(label, score);
            } catch (Exception e) {
                e.printStackTrace();
                return new SentimentAnalysisDTO("ERROR", 0.0);
            }
    }
}
