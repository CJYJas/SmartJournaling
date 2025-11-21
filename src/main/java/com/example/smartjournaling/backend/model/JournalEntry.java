package com.example.smartjournaling.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "journal")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String date; // stored as "YYYY-MM-DD"

    @Column(columnDefinition = "TEXT")
    private String content;

    public JournalEntry() {}

    public JournalEntry(String email, String date, String content) {
        this.email = email;
        this.date = date;
        this.content = content;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
