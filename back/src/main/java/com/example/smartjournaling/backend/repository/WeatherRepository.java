package com.example.smartjournaling.backend.repository;

import java.util.List;
import java.util.Optional; // IMPORTANT: Add Optional import

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.model.WeatherModel;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherModel, Long> {
    
    /**
     * Primary lookup for UPSERT logic: finds the latest weather entry by
     * Location ID and Date, wrapped in Optional for safety.
     * This supports the WeatherService logic to prevent duplicate daily inserts.
     */
    Optional<WeatherModel> findByLocationIdAndDate(String locationId, String date);

    // Existing method, updated to return Optional for consistency in single-record lookups
    /**
     * Finds the latest (top) weather entry by Location Name and Date.
     * Changed to return Optional<WeatherModel> for better practice.
     */
    Optional<WeatherModel> findTopByLocationNameAndDateOrderByIdDesc(String locationName, String date);

    /**
     * Finds all weather records between two dates, ordered chronologically.
     * This is used by the WeatherWeekSummaryService.
     */
    List<WeatherModel> findByDateBetweenOrderByDateAsc(String startDate, String endDate);
}