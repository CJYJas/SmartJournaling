package com.example.smartjournaling.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.model.WeatherModel;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherModel, Long> {
    WeatherModel findTopByLocationNameAndDateOrderByIdDesc(String locationName, String date);

    List<WeatherModel> findByDateBetweenOrderByDateAsc(String startDate, String endDate);
}
