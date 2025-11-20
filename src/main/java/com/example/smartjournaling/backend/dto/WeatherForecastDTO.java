//DTO (Data Transfer Object) extracts only needed field from DAO and send it cleanly
package com.example.smartjournaling.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherForecastDTO {

    private Location location;
    private String date;

    @JsonProperty("morningForecast")
    private String morningForecast;

    @JsonProperty("afternoonForecast")
    private String afternoonForecast;

    @JsonProperty("nightForecast")
    private String nightForecast;

    @JsonProperty("summaryForecast")
    private String summaryForecast;

    @JsonProperty("summaryWhen")
    private String summaryWhen;

    @JsonProperty("minTemp")
    private Double minTemp;

    @JsonProperty("maxTemp")
    private Double maxTemp;

    // ---- Inner class Location ----
    public static class Location {
        @JsonProperty("location_id")
        private String locationId;

        @JsonProperty("location_name")
        private String locationName;

        public Location() {
            this.locationId = "101";
            this.locationName = "Kuala Lumpur";
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
    }

    // ---- Getters & Setters for DTO ----
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
