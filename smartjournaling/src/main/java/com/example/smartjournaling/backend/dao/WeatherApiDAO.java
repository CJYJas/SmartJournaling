package com.example.smartjournaling.backend.dao;

import com.example.smartjournaling.backend.dto.WeatherForecastDTO;
import com.example.smartjournaling.backend.util.API;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class WeatherApiDAO {

    private final API api = new API();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

    public WeatherForecastDTO getTodayWeather(String locationName) {
        try {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String apiURL = "https://api.data.gov.my/weather/forecast/";

            String jsonResponse = api.get(apiURL);

            WeatherForecastDTO[] forecasts = objectMapper.readValue(jsonResponse, WeatherForecastDTO[].class);

            for (WeatherForecastDTO f : forecasts) {
                if (f.getLocation().getLocationName().equalsIgnoreCase(locationName)
                        && f.getDate().equals(today)) {
                    return f;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
