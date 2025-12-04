package com.example.smartjournaling.frontend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8083"; // Backend (journalpage) now runs on 8083

    // --- Auth Endpoints ---
    public String login(String email, String password) {
        try {
            return sendPostRequest("/user/login", Map.of("email", email, "password", password));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Connection failed";
        }
    }

    public String signup(String email, String displayName, String password) {
        try {
            return sendPostRequest("/user/signup", Map.of("email", email, "displayName", displayName, "password", password));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Connection failed";
        }
    }

    // --- Weather Endpoint ---
    public String getWeather(String location) {
        try {
            String endpoint = "/weather/latest?location=" + URLEncoder.encode(location, StandardCharsets.UTF_8);
            String json = sendGetRequest(endpoint);
            
            // Basic JSON parsing to extract "summary_forecast"
            String searchKey = "\"summary_forecast\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex != -1) {
                int valueStart = json.indexOf(":", startIndex) + 1;
                int valueEnd = json.indexOf(",", valueStart);
                if (valueEnd == -1) valueEnd = json.indexOf("}", valueStart);
                
                return json.substring(valueStart, valueEnd).replace("\"", "").trim();
            }
            return "Sunny"; 
        } catch (Exception e) {
            return "Unavailable";
        }
    }

    // --- Journal Endpoints ---
    public String getJournalByDate(String email, String date) {
        try {
            return sendGetRequest("/journal/" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "/" + URLEncoder.encode(date, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String addOrEditTodayJson(String email, String date, String content) {
        try {
            String json = String.format("{\"email\":\"%s\",\"date\":\"%s\",\"content\":\"%s\"}",
                    email.replace("\"", "\\\""), date.replace("\"", "\\\""), content.replace("\"", "\\\""));
            return sendJsonPostRequest("/journal/today", json);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Connection failed";
        }
    }

    // --- HTTP Helpers ---
    private String sendPostRequest(String endpoint, Map<String, String> params) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            postData.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
        }
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        try(OutputStream os = conn.getOutputStream()) {
            os.write(postDataBytes);
        }
        return readResponse(conn);
    }

    private String sendGetRequest(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
            status > 299 ? conn.getErrorStream() : conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line);
        }
        in.close();
        return content.toString();
    }

    private String sendJsonPostRequest(String endpoint, String jsonPayload) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        byte[] postDataBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postDataBytes);
        }
        return readResponse(conn);
    }
}
