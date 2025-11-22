package com.example.smartjournaling.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "weather_week_summary")
public class WeatherWeekSummaryModel {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)

    private Long id;
    private String weathersummary;
    private String weekstart;

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeathersummary() {
        return weathersummary;
    }

    public void setWeathersummary(String weathersummary) {
        this.weathersummary = weathersummary;
    }

    public String getWeekstart() {
        return weekstart;
    }

    public void setWeekstart(String weekstart) {
        this.weekstart = weekstart;
    }
}
