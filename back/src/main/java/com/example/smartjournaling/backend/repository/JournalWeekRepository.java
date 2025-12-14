package com.example.smartjournaling.backend.repository;

import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.model.JournalEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface JournalWeekRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByEmailAndDateBetweenOrderByDateAsc(String email, String start, String end);

    List<JournalEntry> findByEmailAndDate(String email, String date);
}
