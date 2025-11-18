package com.example.smartjournaling.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.example.smartjournaling.backend.service.GreetingService;

import java.util.*;

@RestController
@RequestMapping("/api")
public class WelcomeController {
    
    private final GreetingService greetingService;

    public WelcomeController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/welcome")
    public Map<String, Object> getWelcome(@RequestParam String displayName) {
        String greeting = greetingService.getGreeting();

        Map<String, Object> response = new HashMap<>();
        response.put("greeting", greeting);
        response.put("displayName", displayName);
        response.put("menu", List.of(
                "Create, Edit & View Journals",
                "View Weekly Mood Summary"
        ));

        return response;
    }
}
