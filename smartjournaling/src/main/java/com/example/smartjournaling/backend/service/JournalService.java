package com.example.smartjournaling.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.smartjournaling.backend.model.JournalEntry;
import com.example.smartjournaling.backend.repository.JournalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    public List<JournalEntry> getAllJournals(String email) {
        return journalRepository.findByEmail(email);
    }

    public Optional<JournalEntry> getJournalByDate(String email, String date) {
        return journalRepository.findByEmailAndDate(email, date);
    }

    public JournalEntry createJournal(String email, String date, String content) {
        JournalEntry entry = new JournalEntry(email, date, content);
        return journalRepository.save(entry);
    }

    public Optional<JournalEntry> updateJournal(String email, String date, String content) {
        Optional<JournalEntry> optional = getJournalByDate(email, date);
        optional.ifPresent(entry -> {
            entry.setContent(content);
            journalRepository.save(entry);
        });
        return optional;
    }

    public String addOrEditToday(String email, String content) {
        String today = LocalDate.now().toString();
        Optional<JournalEntry> optional = getJournalByDate(email, today);
        if (optional.isPresent()) {
            updateJournal(email, today, content);
            return "Today's journal updated!";
        } else {
            createJournal(email, today, content);
            return "New journal created for today!";
        }
    }
}
