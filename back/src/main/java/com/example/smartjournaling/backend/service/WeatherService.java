package com.example.smartjournaling.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.smartjournaling.backend.dao.WeatherApiDAO;
import com.example.smartjournaling.backend.dto.WeatherForecastDTO;
import com.example.smartjournaling.backend.model.WeatherModel;
import com.example.smartjournaling.backend.repository.WeatherRepository;

@Service
public class WeatherService {

    @Autowired
    private WeatherApiDAO dao;

    @Autowired
    private WeatherRepository repo;
    
    /**
     * Fetches today's weather from the external API and saves/updates the record
     * in the database (UPSERT logic).
     */
public WeatherModel fetchAndSaveTodayWeather(String location) {

        // 1. Fetch the DTO from the external weather API
    WeatherForecastDTO dto = dao.getTodayWeather(location);
    if (dto == null || dto.getLocation() == null || dto.getDate() == null) {
    return null; // Cannot proceed without core data
    }

        String todayDate = dto.getDate(); // Use the date returned by the API for correctness
        String locationId = dto.getLocation().getLocationId();
        
        // 2. CHECK: See if a record already exists for this date and location ID.
        // Assuming WeatherRepository has: Optional<WeatherModel> findByLocationIdAndDate(String locationId, String date);
        Optional<WeatherModel> existingEntity = repo.findByLocationIdAndDate(locationId, todayDate);
        
        WeatherModel entity;
        
        if (existingEntity.isPresent()) {
            // Data exists: UPDATE the existing row instead of creating a new one.
            entity = existingEntity.get();
        } else {
            // Data does not exist: CREATE a new entity.
            entity = new WeatherModel();
            entity.setLocationId(locationId);
            entity.setLocationName(dto.getLocation().getLocationName());
            entity.setDate(todayDate);
        }

        // 3. MAP: Map the latest data from the DTO to the entity (update or new)
    entity.setMorningForecast(dto.getMorningForecast());
    entity.setAfternoonForecast(dto.getAfternoonForecast());
    entity.setNightForecast(dto.getNightForecast());
    entity.setSummaryForecast(dto.getSummaryForecast());
    entity.setSummaryWhen(dto.getSummaryWhen());
    entity.setMinTemp(dto.getMinTemp());
    entity.setMaxTemp(dto.getMaxTemp());

        // 4. SAVE: Spring Data JPA handles INSERT vs. UPDATE based on the primary key (ID).
        // Since we retrieve the existing ID, it performs an UPDATE.
    return repo.save(entity);
    }

    public Optional<WeatherModel> getWeatherForDate(String location, String date) {
    // Uses the existing findTopByLocationNameAndDateOrderByIdDesc in WeatherRepository
    return repo.findTopByLocationNameAndDateOrderByIdDesc(location, date);
    }
}