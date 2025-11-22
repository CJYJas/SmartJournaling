package com.example.smartjournaling.backend.service;

import com.example.smartjournaling.backend.dao.WeatherApiDAO;
import com.example.smartjournaling.backend.dto.WeatherForecastDTO;
import com.example.smartjournaling.backend.model.WeatherModel;
import com.example.smartjournaling.backend.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Autowired
    private WeatherApiDAO dao;

    @Autowired
    private WeatherRepository repo;

    public WeatherModel fetchAndSaveTodayWeather(String location) {
        WeatherForecastDTO dto = dao.getTodayWeather(location);
        if (dto == null)
            return null;

        WeatherModel entity = new WeatherModel();
        entity.setLocationId(dto.getLocation().getLocationId());
        entity.setLocationName(dto.getLocation().getLocationName());
        entity.setDate(dto.getDate());
        entity.setMorningForecast(dto.getMorningForecast());
        entity.setAfternoonForecast(dto.getAfternoonForecast());
        entity.setNightForecast(dto.getNightForecast());
        entity.setSummaryForecast(dto.getSummaryForecast());
        entity.setSummaryWhen(dto.getSummaryWhen());
        entity.setMinTemp(dto.getMinTemp());
        entity.setMaxTemp(dto.getMaxTemp());

        return repo.save(entity);
    }
}