package com.example.smartjournaling.backend.service;

import com.example.smartjournaling.backend.repository.WeatherWeekSummaryRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Service;

import com.example.smartjournaling.backend.model.WeatherModel;
import com.example.smartjournaling.backend.model.WeatherWeekSummaryModel;
import com.example.smartjournaling.backend.repository.WeatherRepository;

@Service
public class WeatherWeekSummaryService {
    private final WeatherWeekSummaryRepository WeatherWeekSummaryRepository;
    private final WeatherRepository WeatherRepository;

    public WeatherWeekSummaryService(
            WeatherWeekSummaryRepository WeatherWeekSummaryRepository,
            WeatherRepository WeatherRepository) {
        this.WeatherWeekSummaryRepository = WeatherWeekSummaryRepository;
        this.WeatherRepository = WeatherRepository;
    }

    public WeatherWeekSummaryModel getandSaveLatestWeekWeatherSummary() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);

        String start = monday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = sunday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<WeatherModel> latestWeekWeatherContent = WeatherRepository.findByDateBetweenOrderByDateAsc(start, end);

        List<String> weeklySummaryForecast = latestWeekWeatherContent.stream()
                .map(WeatherModel::getSummaryForecast)
                .toList();

        Map<String, Integer> summaryCount = new HashMap<>();

        for (String summary : weeklySummaryForecast) {
            summaryCount.put(summary, summaryCount.getOrDefault(summary, 0) + 1);
        }

        String mostFrequentSummary = Collections.max(summaryCount.entrySet(), Map.Entry.comparingByValue()).getKey();

        WeatherWeekSummaryModel entity = new WeatherWeekSummaryModel();
        entity.setWeekstart(start);
        entity.setWeathersummary(mostFrequentSummary);

        return WeatherWeekSummaryRepository.save(entity);
    }
}