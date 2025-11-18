package com.example.smartjournaling.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.smartjournaling.backend.model.JournalEntry;
import com.example.smartjournaling.backend.service.JournalService;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private JournalService journalService;

    // GET all journals for a user
    @GetMapping("/{email}")
    public List<JournalEntry> getAllJournals(@PathVariable String email) {
        return journalService.getAllJournals(email);
    }

    // GET journal by date
    @GetMapping("/{email}/{date}")
    public JournalEntry getJournalByDate(@PathVariable String email,
                                         @PathVariable String date) {
        return journalService.getJournalByDate(email, date)
                .orElse(null);
    }

    // POST create or edit today's journal
    @PostMapping("/today")
    public String addOrEditToday(@RequestBody JournalEntry body) {
        return journalService.addOrEditToday(body.getEmail(), body.getContent());
    }
}
