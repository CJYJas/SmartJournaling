package com.example.smartjournalling.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournalling.Backend.model.JournalSentimentModel;

import java.util.*;

import java.util.List;

@Repository
public interface JournalSentimentRepository extends JpaRepository<JournalSentimentModel, Long> {

    // 1️⃣ Get all weekly sentiments for a user, newest week first
    List<JournalSentimentModel> findByEmailOrderByWeekStartDesc(String email);

    // 2️⃣ Get the latest week sentiment for a user
    JournalSentimentModel findTopByEmailOrderByWeekStartDesc(String email);

    // 3️⃣ Get weekly sentiment for a user for a specific weekStart
    Optional<JournalSentimentModel> findByEmailAndWeekStart(String email, String weekStart);
}


