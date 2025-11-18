package com.example.smartjounaling.backend.service;

import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Service
public class GreetingService {
    
    public String getGreeting() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT+8"));
        int hour = now.getHour();

        if (hour >= 0 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }
}
