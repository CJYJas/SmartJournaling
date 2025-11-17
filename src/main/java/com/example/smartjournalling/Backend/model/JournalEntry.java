package com.example.smartjournalling.Backend.model;

public class JournalEntry {
    String userid;
    String date;
    private String content;

    public JournalEntry(String userid, String date, String content) {
        this.userid = userid;
        this.date = date;
        this.content = content;
    }

    public String getUserid() { 
        return userid; 
    }
    public void setUserid(String userid) { 
        this.userid = userid; 
    }
    public String getDate() {
        return date; 
    }
    public void setDate(String date) { 
        this.date = date; 
    }
    public String getContent() { 
        return content; 
    }
    public void setContent(String content) {
         this.content = content; 
    }

    @Override
    public String toString() {
        return userid + "|" + date + "|" + content;
    }

    public static JournalEntry fromString(String line) {
        String[] parts = line.split("\\|");
        return new JournalEntry(parts[0], parts[1], parts[2]);
    }
}
