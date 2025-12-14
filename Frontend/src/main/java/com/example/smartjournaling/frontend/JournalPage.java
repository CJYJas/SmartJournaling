package com.example.smartjournaling.frontend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.smartjournaling.App;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class JournalPage {
    private final App app;
    private final ApiClient api;
    
    private ListView<String> dateListView;
    private VBox entryView;
    private TextArea entryTextArea;
    private Label weatherLabel;
    private Label moodLabel;
    private Button createButton;
    private Button editButton;
    private Button viewButton;
    private Button saveButton;
    
    private String selectedDate;
    private Map<String, String> journalEntries;
    private boolean isEditMode = false;
    
    // Sentiment fields
    private Map<String, String> sentimentlabel;
    private Map<String, String> sentimentscore;
    
    // FIX: Local cache for weather summary to prevent duplicate API calls/database writes
    private Map<String, String> weatherCache; 

    public JournalPage(App app, ApiClient api) {
        this.app = app;
        this.api = api;
        this.selectedDate = LocalDate.now().toString();
        this.journalEntries = new HashMap<>();
        this.sentimentlabel = new HashMap<>();
        this.sentimentscore = new HashMap<>();
        this.weatherCache = new HashMap<>();
        
        // --- MOCK DATA REMOVED --- 
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("magical-gradient");
        
        // Top bar with back button
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Main content: split view
        HBox mainContent = new HBox(0);
        mainContent.setAlignment(Pos.TOP_LEFT);
        
        // Left sidebar - date list
        VBox sidebar = createDateSidebar(); 
        
        // Right side - entry view
        entryView = createEntryView();
        
        mainContent.getChildren().addAll(sidebar, entryView);
        HBox.setHgrow(entryView, Priority.ALWAYS);
        
        root.setCenter(mainContent);
        
        // Load today's entry by default
        loadEntry(selectedDate);
        
        return root;
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.getStyleClass().add("weather-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        
        Button backButton = new Button("← Back to Home");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> app.showWelcome(app.getCurrentUserName()));
        
        Text title = new Text("My Journal");
        title.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(backButton, title, spacer);
        return topBar;
    }

    private VBox createDateSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("journal-sidebar");
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20));
        
        Text sidebarTitle = new Text("Dates");
        sidebarTitle.setStyle("-fx-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        dateListView = new ListView<>();
        dateListView.getStyleClass().add("date-list");
        
        // --- Generate 7 days dynamically ---
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        List<String> dates = new ArrayList<>();
        // Loop for 7 days (today + 6 previous days)
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            if (i == 0) {
                dates.add("Today");
            } else {
                dates.add(date.format(formatter));
            }
        }
        
        dateListView.getItems().addAll(dates);
        dateListView.getSelectionModel().select(0); // Select "Today" by default
        
        dateListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Determine the correct date key (YYYY-MM-DD)
                String dateKey = newVal.equals("Today") ? LocalDate.now().toString() : newVal;
                loadEntry(dateKey);
            }
        });
        
        VBox.setVgrow(dateListView, Priority.ALWAYS);
        sidebar.getChildren().addAll(sidebarTitle, dateListView);
        
        return sidebar;
    }

    private VBox createEntryView() {
        VBox view = new VBox(20);
        view.getStyleClass().add("entry-view");
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);
        
        // Smart features section
        HBox smartFeatures = new HBox(30);
        smartFeatures.setAlignment(Pos.CENTER);
        
        // Weather widget
        VBox weatherWidget = new VBox(5);
        weatherWidget.getStyleClass().add("smart-widget");
        weatherWidget.setAlignment(Pos.CENTER);
        Label weatherIcon = new Label("☁️");
        weatherIcon.setStyle("-fx-font-size: 32px;");
        weatherLabel = new Label("Loading weather...");
        weatherLabel.setStyle("-fx-text-fill: #6a4c93; -fx-font-weight: bold;");
        weatherWidget.getChildren().addAll(weatherIcon, weatherLabel);
        
        // Mood analysis widget
        VBox moodWidget = new VBox(5);
        moodWidget.getStyleClass().add("smart-widget");
        moodWidget.setAlignment(Pos.CENTER);
        // FIX: Add minimum width to ensure the box is displayed correctly
        moodWidget.setMinWidth(150); 
        Label moodIcon = new Label("😊");
        moodIcon.setStyle("-fx-font-size: 32px;");
        moodLabel = new Label("Analyzing Mood...");
        moodLabel.setStyle("-fx-text-fill: #6a4c93; -fx-font-weight: bold;");
        moodWidget.getChildren().addAll(moodIcon, moodLabel);
        
        smartFeatures.getChildren().addAll(weatherWidget, moodWidget);
        
        // Entry card
        VBox entryCard = new VBox(15);
        entryCard.getStyleClass().add("entry-card");
        entryCard.setAlignment(Pos.TOP_CENTER);
        entryCard.setPadding(new Insets(25));
        
        Text entryTitle = new Text("Journal Entry");
        entryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #6a4c93;");
        
        // Text area for entry
        entryTextArea = new TextArea();
        entryTextArea.getStyleClass().add("entry-textarea");
        entryTextArea.setPromptText("Write your thoughts here...");
        entryTextArea.setPrefRowCount(12);
        entryTextArea.setWrapText(true);
        entryTextArea.setEditable(false);
        VBox.setVgrow(entryTextArea, Priority.ALWAYS);
        
        // Buttons
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        
        createButton = new Button("Create Journal");
        createButton.getStyleClass().add("button-primary");
        createButton.setOnAction(e -> enableEditMode());
        
        editButton = new Button("Edit Journal");
        editButton.getStyleClass().add("button-primary");
        editButton.setOnAction(e -> enableEditMode());
        
        viewButton = new Button("View Journal");
        viewButton.getStyleClass().add("button-secondary");
        viewButton.setOnAction(e -> disableEditMode());
        
        saveButton = new Button("Save");
        saveButton.getStyleClass().add("button-primary");
        saveButton.setOnAction(e -> saveEntry());
        
        buttonBar.getChildren().addAll(createButton, editButton, viewButton, saveButton);
        
        entryCard.getChildren().addAll(entryTitle, entryTextArea, buttonBar);
        
        view.getChildren().addAll(smartFeatures, entryCard);
        
        return view;
    }

    private void loadEntry(String dateKey) {
        selectedDate = dateKey;
        String entry = journalEntries.get(dateKey);
        
        String email = app.getCurrentUserName();
        if (email == null || email.isBlank() || email.equals("Guest")) {
            email = "guest@example.com"; 
        }

        // 1. Try to fetch entry from backend (Content)
        boolean contentFetched = false;
        try {
            String resp = api.getJournalByDate(email, dateKey);
            
            if (resp != null && !resp.isBlank()) {
                String searchKey = "\"content\":\"";
                int idx = resp.indexOf(searchKey);
                
                if (idx != -1) {
                    int start = idx + searchKey.length();
                    int end = resp.indexOf("\"", start); 
                    
                    if (end != -1) {
                        entry = resp.substring(start, end).replace("\\\"", "\""); 
                        journalEntries.put(dateKey, entry);
                        contentFetched = true;
                    }
                }
            }
        } catch (Exception e) {
            // ignore and use local mock
        }
        
        // 2. Fetch sentiment ONLY IF content was successfully retrieved AND sentiment data is missing locally.
        
        // Clean up old sentiment data if no content was found.
        if (!contentFetched) {
            sentimentlabel.remove(dateKey);
            sentimentscore.remove(dateKey);
        }

        // --- FIX: Only call the analysis API if the entry exists AND we don't have the result cached yet. ---
        boolean isSentimentCached = sentimentlabel.containsKey(dateKey) && sentimentscore.containsKey(dateKey);

        if (contentFetched && !isSentimentCached) {
            try {
                String sentimentResp = api.getSentimentAnalysis(email, dateKey);
                System.out.println("DEBUG: Sentiment Response on Load for " + dateKey + ": " + sentimentResp);
                if (sentimentResp != null && !sentimentResp.isBlank() && !sentimentResp.startsWith("Error")) {
                    parseAndCacheSentiment(dateKey, sentimentResp);
                } else {
                    System.err.println("Sentiment API returned null/error for " + dateKey);
                    sentimentlabel.remove(dateKey);
                    sentimentscore.remove(dateKey);
                }
            } catch (Exception e) {
                System.err.println("Error fetching sentiment on load: " + e.getMessage());
                sentimentlabel.remove(dateKey);
                sentimentscore.remove(dateKey);
            }
        }
        // --- END FIX ---
        
        boolean isToday = dateKey.equals(LocalDate.now().toString());
        
        // ... (Button logic remains the same) ...
        if (entry == null || entry.isEmpty()) {
            // Empty entry
            entryTextArea.setText("");
            if (isToday) {
                createButton.setVisible(true);
                createButton.setManaged(true);
                editButton.setVisible(false);
                editButton.setManaged(false);
                viewButton.setVisible(false);
                viewButton.setManaged(false);
                saveButton.setVisible(false);
                saveButton.setManaged(false);
                entryTextArea.setEditable(false);
            } else {
                hideAllButtons();
                entryTextArea.setEditable(false);
            }
        } else {
            // Has entry
            entryTextArea.setText(entry);
            if (isToday) {
                createButton.setVisible(false);
                createButton.setManaged(false);
                editButton.setVisible(true);
                editButton.setManaged(true);
                viewButton.setVisible(true);
                viewButton.setManaged(true);
                saveButton.setVisible(false);
                saveButton.setManaged(false);
                entryTextArea.setEditable(false);
            } else {
                hideAllButtons();
                entryTextArea.setEditable(false);
            }
        }
        
        // 3. Load and display smart features (Weather and Mood)
        updateSmartFeatures(dateKey);
    }

    private void enableEditMode() {
        entryTextArea.setEditable(true);
        createButton.setVisible(false);
        createButton.setManaged(false);
        editButton.setVisible(false);
        editButton.setManaged(false);
        viewButton.setVisible(false);
        viewButton.setManaged(false);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        isEditMode = true;
    }

    private void disableEditMode() {
        entryTextArea.setEditable(false);
        createButton.setVisible(false);
        createButton.setManaged(false);
        editButton.setVisible(true);
        editButton.setManaged(true);
        viewButton.setVisible(true);
        viewButton.setManaged(true);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        isEditMode = false;
    }

    private void hideAllButtons() {
        createButton.setVisible(false);
        createButton.setManaged(false);
        editButton.setVisible(false);
        editButton.setManaged(false);
        viewButton.setVisible(false);
        viewButton.setManaged(false);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
    }

    private void saveEntry() {
        String content = entryTextArea.getText();
        journalEntries.put(selectedDate, content);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Journal entry saved successfully! Analyzing sentiment...");
        alert.show(); 

        String email = app.getCurrentUserName();
        if (email == null || email.isBlank() || email.equals("Guest")) {
            email = "guest@example.com";
        }

        // 1. Save entry to backend
        try {
            api.addOrEditTodayJson(email, selectedDate, content);
        } catch (Exception e) {
            System.err.println("Error saving journal: " + e.getMessage());
        }
        
        // 2. Trigger sentiment analysis and update local cache (This runs immediately after saving, as intended)
        // NOTE: When saving, we always call the analysis API, as the content has changed and requires a fresh analysis/update to the database record.
        try {
            String sentimentResp = api.getSentimentAnalysis(email, selectedDate);
            System.out.println("DEBUG: Sentiment Response on Save for " + selectedDate + ": " + sentimentResp);
            if (sentimentResp != null && !sentimentResp.isBlank() && !sentimentResp.startsWith("Error")) {
                parseAndCacheSentiment(selectedDate, sentimentResp);
            } else {
                System.err.println("Sentiment API returned null/error after save.");
                sentimentlabel.remove(selectedDate);
                sentimentscore.remove(selectedDate);
            }
        } catch (Exception e) {
            System.err.println("Error getting sentiment: " + e.getMessage());
        }
        
        alert.close();
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Journal entry saved and analysis complete!");
        alert.showAndWait();
        
        // Switch back to view mode and reload to update mood/buttons
        disableEditMode();
        loadEntry(selectedDate);
    }
    
    private void parseAndCacheSentiment(String dateKey, String jsonResp) {
        String label = "N/A"; // Default to N/A for parsing failures
        String score = "";
        
        // --- Refined Crude JSON parsing for "sentimentlabel" ---
        String labelKey = "\"sentimentlabel\"";
        int labelIdx = jsonResp.indexOf(labelKey);
        if (labelIdx != -1) {
            int start = jsonResp.indexOf(":", labelIdx) + 1;
            
            int actualStart = start;
            if (jsonResp.substring(start).trim().startsWith("\"")) {
                 actualStart = jsonResp.indexOf("\"", start) + 1;
            }
            int end = jsonResp.indexOf("\"", actualStart);
            
            if (end != -1 && actualStart < end) {
                label = jsonResp.substring(actualStart, end).replace("\\\"", "\"").trim();
            }
        }

        // --- Refined Crude JSON parsing for "sentimentscore" ---
        String scoreKey = "\"sentimentscore\"";
        int scoreIdx = jsonResp.indexOf(scoreKey);
        if (scoreIdx != -1) {
            int valueStart = jsonResp.indexOf(":", scoreIdx) + 1;
            
            int scoreValueStart = valueStart;
            boolean isQuoted = jsonResp.substring(scoreValueStart).trim().startsWith("\"");
            if (isQuoted) {
                 scoreValueStart = jsonResp.indexOf("\"", valueStart) + 1;
            }
            
            int commaEnd = jsonResp.indexOf(",", scoreValueStart);
            int braceEnd = jsonResp.indexOf("}", scoreValueStart);
            int quoteEnd = isQuoted ? jsonResp.indexOf("\"", scoreValueStart) : -1;
            
            int end = -1;
            if (isQuoted && quoteEnd != -1) {
                end = quoteEnd;
            } else if (commaEnd != -1 && (braceEnd == -1 || commaEnd < braceEnd)) {
                end = commaEnd;
            } else if (braceEnd != -1) {
                end = braceEnd;
            }

            if (end != -1 && scoreValueStart < end) {
                String rawScore = jsonResp.substring(scoreValueStart, end).trim();
                try {
                    // Attempt to parse as double (assuming 0.0 to 1.0)
                    double val = Double.parseDouble(rawScore);
                    score = String.format("%.0f%%", val * 100);
                } catch (NumberFormatException e) {
                    // If parsing as double fails, try to clean it up and re-parse
                    rawScore = rawScore.replace("\"", "").trim();
                    try {
                        double val = Double.parseDouble(rawScore);
                        score = String.format("%.0f%%", val * 100);
                    } catch (NumberFormatException innerE) {
                        score = "N/A"; // Unable to parse, fallback gracefully
                    }
                }
            }
        }
        
        sentimentlabel.put(dateKey, label);
        sentimentscore.put(dateKey, score);
        
        // Log the final parsed result
        System.out.println("Sentiment parsed for " + dateKey + ": " + label + " - " + score);
    }

    private void updateSmartFeatures(String dateKey) {
        String temperature = "24°C"; 
        
        // --- Weather Feature (API Call) ---
        // FIX: Check local cache first. Only fetch if current date and cache is empty.
        final String todayKey = LocalDate.now().toString();
        
        if (dateKey.equals(todayKey)) {
            String cachedWeather = weatherCache.get(todayKey);
            if (cachedWeather == null) {
                String location = "Kuala Lumpur"; 
                String weatherSummary = api.getWeather(location); 
                weatherCache.put(todayKey, weatherSummary + ", " + temperature);
                weatherLabel.setText(weatherCache.get(todayKey));
            } else {
                weatherLabel.setText(cachedWeather);
            }
        } else {
            // For past dates, use mock data or implement fetching historical data if available on backend
            weatherLabel.setText("Partly Cloudy, " + temperature);
        }
        
        // --- Mood Analysis Feature (Backend Data) ---
        String label = sentimentlabel.getOrDefault(dateKey, "N/A"); 
        String score = sentimentscore.getOrDefault(dateKey, "");

        if (score.isEmpty() || label.equals("N/A") || score.equals("N/A")) {
             moodLabel.setText(label);
        } else {
             moodLabel.setText(label + " - " + score);
        }
    }
}