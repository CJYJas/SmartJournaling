package com.example.smartjournaling.backend.service;

import com.example.smartjournaling.backend.model.User;
import java.io.*;
import java.util.*;

public class UserFileHelper {

    private static final String FILE_PATH = "UserData.txt";

    public static Map<String, User> loadUsersFromFile() {
        Map<String, User> users = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                // skip empty lines
                if (line.trim().isEmpty()) continue;

                String email = line.trim();
                String displayName = br.readLine();
                while (displayName != null && displayName.trim().isEmpty()) { // skip blank lines
                    displayName = br.readLine();
                }

                String password = br.readLine();
                while (password != null && password.trim().isEmpty()) { // skip blank lines
                    password = br.readLine();
                }

                if (displayName != null && password != null) {
                    users.put(email, new User(email, displayName.trim(), password.trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}