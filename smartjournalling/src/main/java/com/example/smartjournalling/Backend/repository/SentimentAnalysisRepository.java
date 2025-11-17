package com.example.smartjournalling.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartjournalling.Backend.model.SentimentAnalysisModel;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysisModel, Long> {

}
