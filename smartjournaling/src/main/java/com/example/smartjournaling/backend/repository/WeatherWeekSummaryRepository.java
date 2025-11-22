package com.example.smartjournaling.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.model.WeatherWeekSummaryModel;

@Repository
public interface WeatherWeekSummaryRepository extends JpaRepository<WeatherWeekSummaryModel, Long> {
    WeatherWeekSummaryModel findTopByWeekstartOrderByIdDesc(String weekstart);
}
