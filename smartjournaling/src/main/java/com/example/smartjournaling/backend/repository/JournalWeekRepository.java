package com.example.smartjournalling.backend.repository;

import org.springframework.stereotype.Repository;

import com.example.smartjournalling.Backend.model.JournalModel;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface JournalWeekRepository extends JpaRepository<JournalModel, Long> {
    List<JournalModel> findByEmailAndDateBetweenOrderByDateAsc(String email, String start, String end);

    List<JournalModel> findByEmailAndDate(String email, String date);
}
