//Java class that represents weather data you fetch from API

package com.example.smartjournaling.backend.model;

//import JPA annotations to save objects directly to database/do not need to write SQL manually

import jakarta.persistence.*;

@Entity
@Table(name = "weather_data")

public class WeatherModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String locationId;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    private String date;

    private String morningForecast;
    private String afternoonForecast;
    private String nightForecast;
    private String summaryForecast;
    private String summaryWhen;
    private Double minTemp;
    private Double maxTemp;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMorningForecast() {
        return morningForecast;
    }

    public void setMorningForecast(String morningForecast) {
        this.morningForecast = morningForecast;
    }

    public String getAfternoonForecast() {
        return afternoonForecast;
    }

    public void setAfternoonForecast(String afternoonForecast) {
        this.afternoonForecast = afternoonForecast;
    }

    public String getNightForecast() {
        return nightForecast;
    }

    public void setNightForecast(String nightForecast) {
        this.nightForecast = nightForecast;
    }

    public String getSummaryForecast() {
        return summaryForecast;
    }

    public void setSummaryForecast(String summaryForecast) {
        this.summaryForecast = summaryForecast;
    }

    public String getSummaryWhen() {
        return summaryWhen;
    }

    public void setSummaryWhen(String summaryWhen) {
        this.summaryWhen = summaryWhen;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }
}
