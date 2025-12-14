package com.example.smartjournaling.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournaling.backend.model.SentimentAnalysisModel; // Import Optional for robust reading

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysisModel, Long> {

    /**
     * Finds a SentimentAnalysisModel by the user's email and the entry date.
     * This method is crucial for the Service Layer's READ (GET) operation
     * to check if sentiment data already exists, preventing duplicates.
     *
     * Spring Data JPA automatically implements this based on the method name.
     */
    Optional<SentimentAnalysisModel> findByEmailAndDate(String email, String date);
}
