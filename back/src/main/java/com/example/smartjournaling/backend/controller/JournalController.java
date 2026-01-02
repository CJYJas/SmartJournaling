package com.example.smartjournaling.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartjournaling.backend.model.JournalEntry;
import com.example.smartjournaling.backend.service.JournalService;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private JournalService journalService;

    // GET all journals for a user
    @GetMapping
    public List<JournalEntry> getAllJournals(@RequestParam String email) {
        return journalService.getAllJournals(email);
    }

    @GetMapping("/by-date")
    public JournalEntry getJournalByDate(
            @RequestParam String email,
            @RequestParam String date) {
        return journalService.getJournalByDate(email, date).orElse(null);
    }

    // POST create or edit today's journal
    @PostMapping("/today")
    public String addOrEditToday(@RequestBody JournalEntry body) {
        return journalService.addOrEditToday(body.getEmail(), body.getContent());
    }
}
