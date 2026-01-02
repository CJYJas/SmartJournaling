package com.example.smartjournaling.backend.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smartjournaling.backend.model.User;
import com.example.smartjournaling.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;
    private final GreetingService greetingService;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> fileUsers; // users loaded from text file
    private static final String FILE_PATH = "UserData.txt";

    public UserService(UserRepository repo, GreetingService greetingService) {
        this.repo = repo;
        this.greetingService = greetingService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.fileUsers = UserFileHelper.loadUsersFromFile(); // load file users
    }

    // ------------------- SIGN UP -------------------
    public String signup(String email, String name, String password) {
        // --- Validate inputs ---
        if (email == null || email.isBlank()) {
            return "Email cannot be empty!";
        }
        if (name == null || name.isBlank()) {
            return "Display name cannot be empty!";
        }
        if (password == null || password.isBlank()) {
            return "Password cannot be empty!";
        }

        // Check if email exists in DB or file
        if (repo.existsById(email) || fileUsers.containsKey(email)) {
            return "Email already exists!";
        }

        // Save to DB
        String hashedPassword = passwordEncoder.encode(password);
        User dbUser = new User(email, name, hashedPassword);
        repo.save(dbUser);

        // Save to text file
        User fileUser = new User(email, name, hashedPassword);
        saveUserToFile(fileUser);

        // Also add to in-memory map to avoid duplicates in same run
        fileUsers.put(email, fileUser);

        return "Sign up successful!";
    }

    // ------------------- LOGIN -------------------
    public String login(String email, String password) {
        User user = repo.findByEmail(email);

        // Check DB first
        if (user != null) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return "Incorrect password!";
            }
        } else {
            // If not in DB, check text file
            user = fileUsers.get(email);
            if (user == null) return "Email does not exist!";
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return "Incorrect password!";
            }
        }

        String greeting = greetingService.getGreeting();
        return greeting + ", " + user.getDisplayName();
    }

    // ------------------- HELPER: SAVE TO TEXT FILE -------------------
    private void saveUserToFile(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) { // append mode
            bw.write(user.getEmail());
            bw.newLine();
            bw.write(user.getDisplayName());
            bw.newLine();
            bw.write(user.getPassword()); // plain text for file
            bw.newLine();
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}