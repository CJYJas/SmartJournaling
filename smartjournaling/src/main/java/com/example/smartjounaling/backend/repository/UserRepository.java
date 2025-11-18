package com.example.smartjounaling.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartjounaling.backend.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}