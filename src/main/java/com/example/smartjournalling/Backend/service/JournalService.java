package com.example.smartjournalling.backend.service;

import org.springframework.stereotype.Service;

import com.example.smartjournalling.backend.model.JournalEntry;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JournalService {

    // Use a single file path for reading and writing
    private final String filePath = "src/main/resources/JournalData.txt";
    private final String delimiter = "\\|"; // split regex for reading
    private final String delimiterWrite = "|"; // delimiter for writing

    // Get all journal entries for a user
    public List<JournalEntry> getAllJournals(String userid) {
        List<JournalEntry> result = new ArrayList<>();
        for (JournalEntry j : readAllJournals()) {
            if (j.getUserid().equals(userid)) {
                result.add(j);
            }
        }
        return result;
    }

    // Get a journal entry for a specific user and date
    public JournalEntry getJournalByDate(String userid, String date) {
        for (JournalEntry j : readAllJournals()) {
            if (j.getUserid().equals(userid) && j.getDate().equals(date)) {
                return j;
            }
        }
        return null;
    }

    // Create a new journal (if not exists)
    public boolean createJournal(JournalEntry entry) {
        List<JournalEntry> journals = readAllJournals();
        boolean exists = journals.stream()
                .anyMatch(j -> j.getUserid().equals(entry.getUserid()) && j.getDate().equals(entry.getDate()));

        if (exists) return false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(entry.getUserid() + delimiterWrite + entry.getDate() + delimiterWrite + entry.getContent());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write journal data", e);
        }
        return true;
    }

    // Update an existing journal
    public boolean updateJournal(JournalEntry entry) {
        List<JournalEntry> journals = readAllJournals();
        boolean updated = false;

        for (JournalEntry j : journals) {
            if (j.getUserid().equals(entry.getUserid()) && j.getDate().equals(entry.getDate())) {
                j.setContent(entry.getContent());
                updated = true;
            }
        }

        if (updated) saveAllJournals(journals);
        return updated;
    }

    // Add or edit today's journal
    public String addOrEditTodayJournal(String userid, String content) {
        String today = LocalDate.now().toString();
        JournalEntry entry = getJournalByDate(userid, today);

        if (entry != null) {
            entry.setContent(content);
            updateJournal(entry);
            return "Today's journal updated!";
        } else {
            createJournal(new JournalEntry(userid, today, content));
            return "New journal created for today!";
        }
    }

    // Check if today's journal exists
    public boolean hasTodayJournal(String userid) {
        String today = LocalDate.now().toString();
        return getJournalByDate(userid, today) != null;
    }

    // Return all journal dates for a user (latest first)
    public List<String> getAllJournalDates(String userid) {
        List<JournalEntry> entries = getAllJournals(userid);
        List<String> dates = new ArrayList<>();
        for (JournalEntry e : entries) {
            dates.add(e.getDate());
        }
        // Sort descending
        dates.sort((a, b) -> b.compareTo(a));
        return dates;
    }

    // Read all journals from file
    private List<JournalEntry> readAllJournals() {
        List<JournalEntry> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(delimiter);
                if (parts.length == 3) {
                    list.add(new JournalEntry(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read journal data", e);
        }
        return list;
    }

    // Save all journals to file
    private void saveAllJournals(List<JournalEntry> journals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (JournalEntry j : journals) {
                writer.write(j.getUserid() + delimiterWrite + j.getDate() + delimiterWrite + j.getContent());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save journal data", e);
        }
    }
}
