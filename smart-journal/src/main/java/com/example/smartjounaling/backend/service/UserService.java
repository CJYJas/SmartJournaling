package com.example.smartjounaling.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smartjounaling.backend.model.User;
import com.example.smartjounaling.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;
    private final GreetingService greetingService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, GreetingService greetingService) {
        this.repo = repo;
        this.greetingService = greetingService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String signup(String email, String name, String password) {
        if (repo.existsById(email)) {
            return "Email already exists!";
        }
        String hashedPassword = passwordEncoder.encode(password);
        repo.save(new User(email, name, hashedPassword));
        return "Sign up successful!";
    }

    public String login(String email, String password) {
        User user = repo.findByEmail(email);
        if (user == null) {
            return "Email does not exist!";
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Incorrect password!";
        }

        String greeting = greetingService.getGreeting();
        return greeting + ", " + user.getDisplayName();
    }
}
