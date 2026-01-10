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
    private static final String BASE_URL = "http://localhost:8080"; 

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

    // --- Weather Endpoints ---
    
    /**
     * Fetches the full JSON response for the latest weather.
     * Used by Welcome.java to extract locationName and summaryForecast.
     */
    public String getLatestWeatherData(String location) {
        try {
            String endpoint = "/weather/latest?location=" + URLEncoder.encode(location, StandardCharsets.UTF_8);
            return sendGetRequest(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetches weather data for a specific date from the database.
     * Targets: GET /weather/by-date?location=...&date=...
     */
    public String getWeatherByDate(String location, String date) {
        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String encodedDate = URLEncoder.encode(date, StandardCharsets.UTF_8);
            String endpoint = "/weather/by-date?location=" + encodedLocation + "&date=" + encodedDate;
            return sendGetRequest(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Weather database connection failed";
        }
    }

    /**
     * Legacy method: Fetches only the summary forecast string.
     */
    public String getWeather(String location) {
        try {
            String json = getLatestWeatherData(location);
            // Basic JSON parsing to extract "summary_forecast" or "summaryForecast"
            String searchKey = "\"summary_forecast\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) startIndex = json.indexOf("\"summaryForecast\"");
            
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
    
    // --- Weather Summary Endpoint ---
    public String getWeeklyWeatherSummary() {
        try {
            String endpoint = "/weather/weekSummary";
            return sendGetRequest(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Weekly weather summary failed";
        }
    }


    public String getJournalByDate(String email, String date) {
        try {
            String endpoint = "/journal/by-date?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                            + "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8);
            return sendGetRequest(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public String addOrEditTodayJson(String email, String date, String content) {
        try {
            String escapedContent = escapeJsonValue(content);
            String json = String.format("{\"email\":\"%s\",\"date\":\"%s\",\"content\":\"%s\"}",
                    email, date, escapedContent);
            return sendJsonPostRequest("/journal/today", json);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Connection failed";
        }
    }

    public String updateJournalForDate(String email, String date, String content) {
        try {
            String escapedContent = escapeJsonValue(content);
            String json = String.format("{\"email\":\"%s\",\"date\":\"%s\",\"content\":\"%s\"}",
                    email, date, escapedContent);
            return sendJsonPostRequest("/journal/update", json);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to update journal";
        }
    }
    
    // --- Sentiment Endpoints ---
    public String getSentimentAnalysis(String email, String date) {
        try {
            String json = String.format("{\"email\":\"%s\",\"date\":\"%s\"}", email, date);
            return sendJsonPostRequest("/journal/analyze", json);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Sentiment connection failed";
        }
    }
    
    // --- Weekly Summary Endpoints ---
    public String computeWeeklySentiment(String email) {
        try {
            String json = String.format("{\"email\":\"%s\"}", email);
            return sendJsonPostRequest("/journal/weekly-sentiment", json);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Weekly compute failed";
        }
    }

    public String getWeeklySentiment(String email) {
        try {
            String endpoint = "/journal/weekly-sentiment?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
            return sendGetRequest(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Weekly sentiment retrieve failed";
        }
    }

    // --- HTTP Helpers ---
    private String escapeJsonValue(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
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