package com.example.smartjournaling.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartjournaling.backend.model.WeatherWeekSummaryModel;
import com.example.smartjournaling.backend.service.WeatherWeekSummaryService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/weather")

public class WeatherWeekSummaryController {
    private final WeatherWeekSummaryService weatherWeekSummaryService;

    public WeatherWeekSummaryController(WeatherWeekSummaryService weatherWeekSummaryService) {
        this.weatherWeekSummaryService = weatherWeekSummaryService;
    }

    @GetMapping("/weekSummary")
    public WeatherWeekSummaryModel getWeatherWeekSummary() {
        return weatherWeekSummaryService.getandSaveLatestWeekWeatherSummary();
    }

}