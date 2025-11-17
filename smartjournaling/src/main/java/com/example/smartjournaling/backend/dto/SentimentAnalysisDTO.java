package com.example.smartjournaling.backend.dto;

public class SentimentAnalysisDTO {
    private final String sentimentlabel;
    private final double sentimentscore;
    
    public SentimentAnalysisDTO(String sentimentlabel, double sentimentscore) {
        this.sentimentlabel = sentimentlabel;
        this.sentimentscore = sentimentscore;
    }

    public String getSentimentlabel() {
        return sentimentlabel;
    }
    public double getSentimentscore() {
        return sentimentscore;
    } 
}
