package com.example.smartjournalling.Backend.dto;

public class JournalWeekSummaryDTO {
    private final String label;
    private final double score;

    public JournalWeekSummaryDTO(String label, double score) {
        this.label = label;
        this.score = score;
    }

    public String getLabel() {
        return label;
    }
    public double getScore() {
        return score;
    }    
}
