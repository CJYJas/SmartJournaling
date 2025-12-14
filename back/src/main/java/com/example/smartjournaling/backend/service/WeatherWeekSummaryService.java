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
    
    /**
     * Helper method to determine the ISO start date (Monday) of the current week.
     */
    private String getCurrentWeekStart() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        return monday.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * READ: Retrieves the existing weather summary for the current week.
     * The Controller should call this via a GET request.
     */
    public Optional<WeatherWeekSummaryModel> readLatestWeekSummary() {
        String start = getCurrentWeekStart();
        
        // FIX: Call the correct method signature as defined in your repository interface.
        return WeatherWeekSummaryRepository.findTopByWeekstartOrderByWeekstartDesc(start);
    }
    
    /**
     * COMPUTE & SAVE (UPSERT): Computes the weather summary if it doesn't exist, 
     * or returns the existing one.
     */
    public WeatherWeekSummaryModel getandSaveLatestWeekWeatherSummary() {

        String start = getCurrentWeekStart();
        
        // FIX: Call the correct method signature here too.
        Optional<WeatherWeekSummaryModel> existingSummary = WeatherWeekSummaryRepository.findTopByWeekstartOrderByWeekstartDesc(start);
        
        if (existingSummary.isPresent()) {
            // Data found! Return the existing row, preventing duplication.
            return existingSummary.get();
        }
        
        LocalDate monday = LocalDate.parse(start); // We already have the date
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);

        String end = sunday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<WeatherModel> latestWeekWeatherContent = WeatherRepository.findByDateBetweenOrderByDateAsc(start, end);
        
        // Handle case where no daily weather data exists for the week
        if (latestWeekWeatherContent.isEmpty()) {
             WeatherWeekSummaryModel defaultEntity = new WeatherWeekSummaryModel();
             defaultEntity.setWeekstart(start);
             defaultEntity.setWeathersummary("No daily data available.");
             return defaultEntity;
        }

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
        // Save/Insert the new entity
        return WeatherWeekSummaryRepository.save(entity);
            }
        }