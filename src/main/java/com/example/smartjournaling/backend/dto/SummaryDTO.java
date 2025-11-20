//SummaryDTO is to fetch only summary from WeatherForecastDTO instead of the entire WeaterForecastDTO
package com.example.smartjournaling.backend.dto;

public class SummaryDTO {

    // private field
    private String summaryForecast;

    // constructor to create object
    public SummaryDTO(String summaryForecast) {
        this.summaryForecast = summaryForecast;
    }
    // getter and setter

    public String getSummaryForecast() {
        return summaryForecast;
    }

    public String setSummaryForecast() {
        return summaryForecast;
    }
}
