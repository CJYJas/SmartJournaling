package com.example.smartjournaling.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "journal_weekly_sentiment",
       uniqueConstraints = @UniqueConstraint(columnNames = {"email", "week_start"}))
public class JournalSentimentModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;          // store userId directly for now

    @Column(name = "week_start")
    private String weekStart;     // start of the week

    private String sentimentLabel;   // overall weekly label
    private Double sentimentScore;   // aggregated score

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email;}

    public String getWeekStart() { return weekStart; }
    public void setWeekStart(String weekStart) { this.weekStart = weekStart; }

    public String getSentimentLabel() { return sentimentLabel; }
    public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
}

