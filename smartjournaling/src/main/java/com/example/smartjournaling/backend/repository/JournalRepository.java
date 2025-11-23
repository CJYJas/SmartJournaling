package com.example.smartjournaling.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smartjournaling.backend.model.JournalEntry;

import java.util.List;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByEmail(String email);

    Optional<JournalEntry> findByEmailAndDate(String email, String date);
}
