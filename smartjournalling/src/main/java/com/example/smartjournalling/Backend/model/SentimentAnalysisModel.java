package com.example.smartjournalling.Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.*;

@Entity
@Table(name = "sentiment_analysis")
public class SentimentAnalysisModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String date;
    private String sentimentlabel;
    private Double sentimentscore;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getSentimentlabel() {
        return sentimentlabel;
    }
    public void setSentimentlabel(String sentimentlabel) {
        this.sentimentlabel = sentimentlabel;
    }
    public Double getSentimentscore() {
        return sentimentscore;
    }
    public void setSentimentscore(Double sentimentscore) {
        this.sentimentscore = sentimentscore;
    }
}