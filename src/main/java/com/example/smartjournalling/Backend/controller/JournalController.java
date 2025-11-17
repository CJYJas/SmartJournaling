package com.example.smartjournalling.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.smartjournalling.backend.model.JournalEntry;
import com.example.smartjournalling.backend.service.JournalService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journals")
public class JournalController {

    @Autowired
    private JournalService journalService;

    // Get all past journal dates for a user
    @GetMapping("/{userid}/dates")
    public List<String> getAllJournalDates(@PathVariable String userid) {
    return journalService.getAllJournalDates(userid);
    }


    // Create a new journal for today
    @PostMapping("/today")
    public String createOrEditTodayJournal(@RequestBody Map<String, String> body) {
        String userid = body.get("userid");
        String content = body.get("content");
        String today = LocalDate.now().toString();

        JournalEntry existing = journalService.getJournalByDate(userid, today);
        if (existing != null) {
            existing.setContent(content);
            journalService.updateJournal(existing);
            return "Today's journal updated!";
        } else {
            JournalEntry entry = new JournalEntry(userid, today, content);
            journalService.createJournal(entry);
            return "New journal created for today!";
        }
    }

    // Check if today's journal exists
    @GetMapping("/today/{userid}")
    public boolean hasToday(@PathVariable String userid) {
        String today = LocalDate.now().toString();
        return journalService.getJournalByDate(userid, today) != null;
    }

    // Get all journals for a user
    @GetMapping("/{userid}")
    public List<JournalEntry> getAllJournals(@PathVariable String userid) {
        return journalService.getAllJournals(userid);
    }

    // Get journal by specific date
    @GetMapping("/{userid}/{date}")
    public JournalEntry getJournalByDate(@PathVariable String userid, @PathVariable String date) {
        return journalService.getJournalByDate(userid, date);
    }

}
