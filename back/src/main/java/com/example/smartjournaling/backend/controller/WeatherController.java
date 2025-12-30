//Controller receives request from frontend and passes it to the service
package com.example.smartjournaling.backend.controller;

//Controller imports backend classses to call the service and perform backend logic, model to understand the data and DTO send responese back to frontend
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; //Spring injects WeatherService for Controller
import org.springframework.web.bind.annotation.RequestParam;//API returns HTTP response
import org.springframework.web.bind.annotation.RestController;//link URLs to controller methods

import com.example.smartjournaling.backend.dto.SummaryDTO;
import com.example.smartjournaling.backend.model.WeatherModel;
import com.example.smartjournaling.backend.service.WeatherService;

@RestController // handle requests and returns JSON
@RequestMapping("/weather") // final URL will start with /weather

public class WeatherController {
    @Autowired
    private WeatherService weatherService; // Inject WeatherService to call its methods

    @GetMapping("/latest") // Handle GET requests to /weather/latest

    public ResponseEntity<SummaryDTO> getLatestWeather(@RequestParam String location) {
        // Call service to fetch and save today's weather for the given location
        WeatherModel entity = weatherService.fetchAndSaveTodayWeather(location);
        if (entity == null)
            return ResponseEntity.notFound().build(); // Return 404 if no data

        // Create DTO from entity's summary forecast
        SummaryDTO summaryDTO = new SummaryDTO(entity.getSummaryForecast());
        return ResponseEntity.ok(summaryDTO); // Return 200 OK with DTO
    }

   @GetMapping("/by-date")
    public ResponseEntity<WeatherModel> getWeatherByDate(@RequestParam String location, @RequestParam String date) {
        return weatherService.getWeatherForDate(location, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
