package com.example.smartjournaling.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartjournaling.backend.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}