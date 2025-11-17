package com.example.smartjournalling.backend.repository;

import java.io.*;
import java.util.*;

import com.example.smartjournalling.backend.model.JournalEntry;

public class JournalRepository {

    String filename = "src/main/resources/JournalData.txt";

    public List<JournalEntry> getAllEntries() {
        List<JournalEntry> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                entries.add(JournalEntry.fromString(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read journal data", e);

        }
        return entries;
    }

    public void saveAllEntries(List<JournalEntry> entries) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (JournalEntry entry : entries) {
                bw.write(entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read journal data", e);

        }
    }
}

