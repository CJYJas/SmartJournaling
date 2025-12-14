package com.example.smartjournaling.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.model.WeatherWeekSummaryModel;

// In WeatherWeekSummaryRepository.java
@Repository
public interface WeatherWeekSummaryRepository extends JpaRepository<WeatherWeekSummaryModel, Long> {
    Optional<WeatherWeekSummaryModel>findTopByWeekstartOrderByWeekstartDesc(String weekstart);
}