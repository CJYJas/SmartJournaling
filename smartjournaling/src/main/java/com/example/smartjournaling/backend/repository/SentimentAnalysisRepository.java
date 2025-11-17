package com.example.smartjournalling.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournalling.backend.model.SentimentAnalysisModel;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysisModel, Long> {

}
