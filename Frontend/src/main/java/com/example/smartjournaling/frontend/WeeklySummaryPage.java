package com.example.smartjournaling.frontend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.smartjournaling.App;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WeeklySummaryPage {
    private final App app;
    private final ApiClient api;
    
    // UI Elements for dynamic update
    private Text dateRangeLabel;
    private VBox highlightCard;
    private VBox entriesCard;
    private VBox avgMoodCard;
    private VBox moodTimelineContainer;
    private VBox weatherHistoryContainer; // Container for the weather summary display
    
    // Data structures
    private Map<String, String> weeklySummaryData; // Stores highlight, entry count, avg mood, AND weekly weather summary
    private List<Map<String, String>> dailyMoodData; // Stores 7 days of sentiment
    private List<Map<String, String>> dailyWeatherData; // Retained for structure but unused for weather visualization

    // Cache for daily mood data to prevent duplicate POST calls
    private final Map<String, Map<String, String>> dailySentimentCache;
    private boolean weeklySummaryInitialized; // Prevent repeated creation via POST


    public WeeklySummaryPage(App app, ApiClient api) {
        this.app = app;
        this.api = api;
        this.weeklySummaryData = new HashMap<>();
        this.dailyMoodData = new ArrayList<>();
        this.dailyWeatherData = new ArrayList<>();
        this.dailySentimentCache = new ConcurrentHashMap<>();
        this.weeklySummaryInitialized = false;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("magical-gradient");
        
        // Top bar with back button
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Main content
        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        // Title section
        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER);
        Text title = new Text("Weekly Summary");
        title.setStyle("-fx-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        Text subtitle = new Text("Your emotional journey over the past 7 days");
        subtitle.setStyle("-fx-fill: white; -fx-font-size: 14px; -fx-opacity: 0.9;");
        
        titleSection.getChildren().addAll(title, subtitle);
        
        // Stats cards container
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        
        // Initialize dynamic cards with placeholders
        highlightCard = createStatCard("Weekly Highlight", "Loading...", "⏳");
        entriesCard = createStatCard("Entries", "Loading...", "⏳");
        avgMoodCard = createStatCard("Avg Mood", "Loading...", "⏳");
        
        statsCards.getChildren().addAll(highlightCard, entriesCard, avgMoodCard);
        
        // Mood fluctuations timeline container
        moodTimelineContainer = createMoodTimeline();
        
        // Weather history container
        weatherHistoryContainer = createWeatherHistory(); // Modified to return single VBox container
        
        mainContent.getChildren().addAll(titleSection, statsCards, moodTimelineContainer, weatherHistoryContainer);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        mainContent.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
        
        // Start data fetching as soon as the view is created
        fetchAndDisplayData();
        
        return root;
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.getStyleClass().add("weather-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        
        // Back button
        Button backButton = new Button("← Back to Home");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> app.showWelcome(app.getCurrentUserName()));
        
        // Page title
        Text title = new Text("Weekly Summary");
        title.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Date range label (will be updated)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
        dateRangeLabel = new Text(startDate.format(formatter) + " - " + endDate.format(formatter));
        dateRangeLabel.setStyle("-fx-fill: white; -fx-font-size: 14px; -fx-opacity: 0.8;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(backButton, title, spacer, dateRangeLabel);
        return topBar;
    }

    /**
     * Creates a stat card, updates the card's data dynamically in updateStatCards().
     */
    private VBox createStatCard(String title, String value, String emoji) {
        VBox card = new VBox(10);
        card.getStyleClass().add("summary-stat-card");
        card.setPrefSize(180, 120);
        card.setAlignment(Pos.CENTER);
        
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 36px;");
        
        Text cardTitle = new Text(title);
        cardTitle.setStyle("-fx-fill: #888; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Text cardValue = new Text(value);
        cardValue.getProperties().put("card-value", "true"); // Marker for updating later
        cardValue.setStyle("-fx-fill: #6a4c93; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(icon, cardTitle, cardValue);
        return card;
    }
    
    /**
     * Updates the content of the top three summary cards.
     */
    private void updateStatCards() {
        // FIX: Derive entryCount from dailyMoodData size
        // We count entries where the label is not 'N/A' (meaning journal data was found)
        long actualEntries = dailyMoodData.stream().filter(d -> !d.getOrDefault("label", "N/A").equals("N/A")).count();
        String entryCount = actualEntries + " of 7 days";
        
        // FIX: Derive weeklyHighlight from the fetched weekly sentiment label
        String highlight = weeklySummaryData.getOrDefault("weeklyHighlight", "N/A");
        String avgMood = weeklySummaryData.getOrDefault("avgMood", "N/A");

        // Helper to find the Text node by index and update its value
        updateCardValue(highlightCard, highlight, getWeeklyHighlightEmoji(highlight));
        updateCardValue(entriesCard, entryCount, "📝");
        updateCardValue(avgMoodCard, avgMood, "💚");
    }
    
    private void updateCardValue(VBox card, String value, String emoji) {
        for (javafx.scene.Node node : card.getChildren()) {
            if (node instanceof Text && node.getProperties().containsKey("card-value")) {
                ((Text) node).setText(value);
            }
            if (node instanceof Label && ((Label)node).getText().length() == 1) {
                ((Label) node).setText(emoji);
            }
        }
    }


    private VBox createMoodTimeline() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        Text sectionTitle = new Text("Mood Fluctuations");
        sectionTitle.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Timeline container (Placeholder)
        HBox timeline = new HBox(15);
        timeline.setAlignment(Pos.CENTER);
        timeline.getStyleClass().add("timeline-container");
        timeline.getChildren().add(new Text("Loading Mood Timeline..."));
        timeline.getChildren().add(new Region());
        HBox.setHgrow(timeline.getChildren().get(1), Priority.ALWAYS);

        section.getChildren().addAll(sectionTitle, timeline);
        return section;
    }
    
    /**
     * Populates the mood timeline based on fetched daily data.
     */
    private void populateMoodTimeline() {
        HBox timeline = new HBox(15);
        timeline.setAlignment(Pos.CENTER);
        timeline.getStyleClass().add("timeline-container");
        
        // Check if data is truly empty or just unparsed due to failure
        if (dailyMoodData.isEmpty() || (dailyMoodData.size() > 0 && dailyMoodData.get(0).get("label") == null)) {
             timeline.getChildren().add(new Text("No mood data available for the past week."));
        } else {
             for (Map<String, String> day : dailyMoodData) {
                 VBox dayCard = createMoodDayCard(
                    day.getOrDefault("dayName", "N/A"),
                    day.getOrDefault("label", "N/A"),
                    day.getOrDefault("emoji", "😐"),
                    day.getOrDefault("color", "#d3d3d3") // Default gray for N/A
                 );
                 timeline.getChildren().add(dayCard);
             }
        }
        
        // Replace placeholder
        moodTimelineContainer.getChildren().remove(1);
        moodTimelineContainer.getChildren().add(timeline);
    }
    

    private VBox createMoodDayCard(String dayName, String mood, String emoji, String color) {
        VBox card = new VBox(8);
        card.getStyleClass().add("mood-day-card");
        card.setPrefSize(100, 120);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12; -fx-padding: 12;");
        
        Text dayLabel = new Text(dayName);
        dayLabel.setStyle("-fx-fill: #333; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 32px;");
        
        Text moodText = new Text(mood);
        moodText.setStyle("-fx-fill: #555; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(dayLabel, emojiLabel, moodText);
        return card;
    }

    private VBox createWeatherHistory() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        
        Text sectionTitle = new Text("Weather History");
        sectionTitle.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Weather container (Placeholder - will be replaced by updateWeatherSummaryCard)
        VBox summaryContainer = new VBox(10);
        summaryContainer.setAlignment(Pos.CENTER);
        summaryContainer.getChildren().add(new Text("Loading Weather Summary..."));
        
        section.getChildren().addAll(sectionTitle, summaryContainer);
        return section;
    }
    
    /**
     * Populates the weather history display using the single aggregate summary.
     */
    private void populateWeatherHistory() {
        VBox container = (VBox) weatherHistoryContainer.getChildren().get(1);
        container.getChildren().clear();
        
        String summary = weeklySummaryData.getOrDefault("weatherSummary", "No aggregate weather data found.");
        String icon = convertWeatherIcon(summary);
        
        VBox weatherCard = new VBox(15);
        weatherCard.getStyleClass().add("summary-stat-card"); // Reuse stat card styling
        weatherCard.setPrefWidth(300);
        weatherCard.setAlignment(Pos.CENTER);
        weatherCard.setPadding(new Insets(20));

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Text summaryText = new Text(summary);
        summaryText.setStyle("-fx-fill: #6a4c93; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");
        summaryText.setWrappingWidth(250);
        
        Text description = new Text("Predominant condition for the past week.");
        description.setStyle("-fx-fill: #888; -fx-font-size: 12px;");

        weatherCard.getChildren().addAll(iconLabel, summaryText, description);
        container.getChildren().add(weatherCard);
    }
    
    // --- Data Fetching and Parsing ---
    
    private void fetchAndDisplayData() {
        String email = app.getCurrentUserName();
        if (email == null || email.isBlank() || email.equals("Guest")) {
            email = "guest@example.com"; 
        }
        
        // 1. Retrieve Weekly Summary Data (GET call first)
        boolean weeklyParsed = false;
        try {
            String resp = api.getWeeklySentiment(email);
            System.out.println("DEBUG: Weekly Summary Response: " + resp);
            weeklyParsed = parseWeeklySummary(resp);
        } catch (Exception e) {
            System.err.println("Failed to retrieve weekly summary: " + e.getMessage());
        }

        // 2. Only if missing, compute once and re-fetch (avoid duplicate creation on every load)
        if (!weeklyParsed && !weeklySummaryInitialized) {
            try {
                String computeResp = api.computeWeeklySentiment(email);
                System.out.println("DEBUG: Weekly Compute Response (on-demand): " + computeResp);
                String resp = api.getWeeklySentiment(email);
                System.out.println("DEBUG: Weekly Summary Response (after compute): " + resp);
                weeklyParsed = parseWeeklySummary(resp);
            } catch (Exception e) {
                System.err.println("Failed to compute/retrieve weekly summary: " + e.getMessage());
            }
        }
        weeklySummaryInitialized = weeklySummaryInitialized || weeklyParsed;

        // 3. Fetch Daily Moods for Timeline (7 individual POST calls, CACHED)
        List<String> dates = generateLastSevenDays();
        List<Map<String, String>> fetchedDailyMoods = new ArrayList<>();

        for (String date : dates) {
            Map<String, String> moodData;
            
            // FIX: Check cache first to prevent redundant POST calls/DB writes.
            if (dailySentimentCache.containsKey(date)) {
                moodData = dailySentimentCache.get(date);
            } else {
                try {
                    // Call the single-day POST /journal/analyze endpoint for each day
                    String resp = api.getSentimentAnalysis(email, date);
                    System.out.println("DEBUG: Daily Moods Response for " + date + ": " + resp);
                    
                    // Parse the single object response and save to cache
                    moodData = parseSingleDailyMood(resp, date);
                    dailySentimentCache.put(date, moodData);

                } catch (Exception e) {
                    System.err.println("Failed to fetch daily mood for " + date + ": " + e.getMessage());
                    // Add a blank/N/A map for the failed day to maintain the 7-day structure
                    moodData = initializeSingleDailyMood(date);
                }
            }
            fetchedDailyMoods.add(moodData);
        }
        this.dailyMoodData = fetchedDailyMoods;
        populateMoodTimeline();
        
        // 4. Fetch Weather History (Single GET call to correct endpoint)
        try {
            // FIX: Calling the correct controller endpoint: /weather/weekSummary
            String resp = api.getWeeklyWeatherSummary(); 
            System.out.println("DEBUG: Weather History Response: " + resp);
            parseWeatherHistory(resp); 
            populateWeatherHistory();
        } catch (Exception e) {
             System.err.println("Failed to fetch weather history: " + e.getMessage());
             this.dailyWeatherData.clear();
             populateWeatherHistory();
        }
        
        // FINAL UI UPDATE (Relies on all data structures being populated)
        updateStatCards();
    }
    
    private List<String> generateLastSevenDays() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).format(formatter));
        }
        return dates;
    }

    private Map<String, String> initializeSingleDailyMood(String dateKey) {
        LocalDate date = LocalDate.parse(dateKey, DateTimeFormatter.ISO_LOCAL_DATE);
        String dayName = date.format(DateTimeFormatter.ofPattern("EEE"));
        Map<String, String> dayMap = new HashMap<>();
        dayMap.put("dayName", dayName);
        dayMap.put("dateKey", dateKey);
        dayMap.put("label", "N/A");
        dayMap.put("emoji", "😐");
        dayMap.put("color", "#d3d3d3"); 
        return dayMap;
    }

    /**
     * Parses the single object response from the daily /journal/analyze endpoint.
     */
    private Map<String, String> parseSingleDailyMood(String jsonResp, String dateKey) {
        Map<String, String> dayMap = initializeSingleDailyMood(dateKey);
        
        if (jsonResp == null || jsonResp.isBlank() || jsonResp.startsWith("Error")) return dayMap;
        
        // Keys: "sentimentlabel" and "sentimentscore" (based on debug output)
        String label = extractJsonStringValue(jsonResp, "sentimentlabel"); 
        String score = extractJsonNumberValue(jsonResp, "sentimentscore"); 

        if (!label.equals("N/A")) {
            dayMap.put("label", label);
            dayMap.put("emoji", getMoodEmoji(label));
            dayMap.put("color", getMoodColor(label));
            dayMap.put("score", score);
        }
        return dayMap;
    }

    /**
     * Parses the response from /journal/weekly-sentiment.
     */
    private boolean parseWeeklySummary(String jsonResp) {
        if (jsonResp == null || jsonResp.isBlank() || jsonResp.startsWith("Error")) {
             System.err.println("Parsing failed: Response is null, empty, or error.");
             return false;
        }
        
        // Keys are derived from the observed response: "sentimentLabel" and "sentimentScore"
        String weeklySentimentLabel = extractJsonStringValue(jsonResp, "sentimentLabel"); 
        String avgScore = extractJsonNumberValue(jsonResp, "sentimentScore");
        
        // 1. Set Weekly Highlight based on the main sentiment label
        weeklySummaryData.put("weeklyHighlight", weeklySentimentLabel);
        
        // 2. Calculate and set Avg Mood
        try {
            double val = Double.parseDouble(avgScore);
            String avgMoodPercentage = String.format("%.0f%%", val * 100);
            String label = weeklySentimentLabel; // Use the direct label from backend
            weeklySummaryData.put("avgMood", label + " - " + avgMoodPercentage);
        } catch (NumberFormatException e) {
             weeklySummaryData.put("avgMood", "N/A (Score Parse Failed)");
             System.err.println("Error parsing avgMood score: " + avgScore);
        }
        
        System.out.println("Weekly Summary Parsed: " + weeklySummaryData);
           return true;
    }
    
    /**
     * Parses the response from /weather/weekSummary (WeatherWeekSummaryModel).
     * Extracts the single aggregate weathersummary for display.
     */
    private void parseWeatherHistory(String jsonResp) {
        if (jsonResp == null || jsonResp.isBlank() || jsonResp.startsWith("Error")) {
             System.err.println("Parsing Weather History failed: Response is null, empty, or error.");
             return;
        }
        
        // Extract the single weekly summary returned by the backend (e.g., "Ribut petir di beberapa tempat")
        String weatherSummary = extractJsonStringValue(jsonResp, "weathersummary");
        
        if (weatherSummary.equals("N/A")) {
            System.err.println("Weather History Parsing: Could not find 'weathersummary' in the response.");
        }
        
        // Store the aggregate summary in the main data map
        weeklySummaryData.put("weatherSummary", weatherSummary);
    }
    
    private Map<String, Map<String, String>> initializeDailyWeatherMap(List<String> dates) {
        Map<String, Map<String, String>> map = new HashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");

        for (String dateKey : dates) {
            LocalDate date = LocalDate.parse(dateKey, DateTimeFormatter.ISO_LOCAL_DATE);
            String dayName = date.format(dayFormatter);
            Map<String, String> dayMap = new HashMap<>();
            dayMap.put("dayName", dayName);
            dayMap.put("dateKey", dateKey);
            dayMap.put("icon", "❓");
            dayMap.put("temp", "N/A"); 
            map.put(dateKey, dayMap);
        }
        return map;
    }
    
    // --- Crude JSON Extraction Helpers (REFINED FOR CASE-INSENSITIVITY) ---

    private String extractJsonStringValue(String json, String key) {
        // Search for the key case-insensitively using regex principles
        int idx = json.toLowerCase().indexOf(key.toLowerCase());
        if (idx == -1) return "N/A";
        
        // Find the index of the colon that follows the found key substring
        int keyEnd = json.indexOf(":", idx + key.length());
        if (keyEnd == -1) return "N/A";
        
        int start = keyEnd + 1;
        int actualStart = start;
        
        // Find the index of the first non-whitespace character after the colon
        while (actualStart < json.length() && Character.isWhitespace(json.charAt(actualStart))) {
            actualStart++;
        }
        
        // Handle quoted value (most common for strings)
        if (actualStart < json.length() && json.charAt(actualStart) == '"') {
             actualStart++; // Skip starting quote
             int end = json.indexOf("\"", actualStart);
             if (end != -1) {
                 return json.substring(actualStart, end).replace("\\\"", "\"").trim();
             }
        } else {
             // Handle unquoted values (numbers or booleans)
             int commaEnd = json.indexOf(",", actualStart);
             int braceEnd = json.indexOf("}", actualStart);
             
             int end = -1;
             if (commaEnd != -1 && (braceEnd == -1 || commaEnd < braceEnd)) {
                 end = commaEnd;
             } else if (braceEnd != -1) {
                 end = braceEnd;
             }

             if (end != -1) {
                 return json.substring(actualStart, end).trim();
             }
        }
        return "N/A";
    }
    
    private String extractJsonNumberValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int idx = json.toLowerCase().indexOf(key.toLowerCase());
        if (idx == -1) return "0.0"; 
        
        int keyEnd = json.indexOf(":", idx + key.length());
        if (keyEnd == -1) return "0.0";

        int valueStart = keyEnd + 1;
        int scoreValueStart = valueStart;
        
        // Skip leading whitespace and quotes
        while (scoreValueStart < json.length() && Character.isWhitespace(json.charAt(scoreValueStart))) {
            scoreValueStart++;
        }
        if (scoreValueStart < json.length() && json.charAt(scoreValueStart) == '"') {
            scoreValueStart++; // Skip starting quote
        }
        
        // Find the end token (comma or brace, ignoring closing quote if present)
        int commaEnd = json.indexOf(",", scoreValueStart);
        int braceEnd = json.indexOf("}", scoreValueStart);
        int quoteEnd = json.indexOf("\"", scoreValueStart);
        
        int end = json.length(); // Default to end of string
        
        if (commaEnd != -1) end = Math.min(end, commaEnd);
        if (braceEnd != -1) end = Math.min(end, braceEnd);
        if (quoteEnd != -1 && quoteEnd < end) end = quoteEnd; 


        if (end != json.length() && scoreValueStart < end) {
            return json.substring(scoreValueStart, end).replace("\"", "").trim();
        }
        return "0.0";
    }
    
    // --- UI Helper Logic (Mood/Color/Icon Mapping) ---
    private String getMoodEmoji(String label) {
        return switch (label.toUpperCase()) {
            case "POSITIVE" -> "😊";
            case "NEGATIVE" -> "😟";
            case "MIXED" -> "😰";
            case "NEUTRAL" -> "😌";
            default -> "😐";
        };
    }
    
    private String getWeeklyHighlightEmoji(String highlight) {
        if (highlight.toLowerCase().contains("positive")) return "✨";
        if (highlight.toLowerCase().contains("negative") || highlight.toLowerCase().contains("mixed")) return "📉";
        return "😐";
    }

    private String getMoodColor(String label) {
        return switch (label.toUpperCase()) {
            case "POSITIVE" -> "#95e1d3"; // Light Teal/Green
            case "NEGATIVE" -> "#f38181"; // Light Red
            case "MIXED" -> "#ffd3b6";    // Light Orange/Pink
            case "NEUTRAL" -> "#a8e6cf";  // Mint Green
            default -> "#d3d3d3";        // Gray
        };
    }
    
    private String convertWeatherIcon(String backendSummary) {
         if (backendSummary.toLowerCase().contains("sunny") || backendSummary.toLowerCase().contains("clear")) return "☀️";
         if (backendSummary.toLowerCase().contains("cloudy") || backendSummary.toLowerCase().contains("overcast")) return "☁️";
         if (backendSummary.toLowerCase().contains("rain") || backendSummary.toLowerCase().contains("drizzle")) return "🌧️";
         if (backendSummary.toLowerCase().contains("thunderstorm")) return "⛈️";
         return "🌤️"; // Default for partly cloudy/unknown
    }
}