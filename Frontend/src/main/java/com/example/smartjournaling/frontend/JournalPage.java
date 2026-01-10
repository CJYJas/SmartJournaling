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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
    private DatePicker datePicker;
    
    // Class fields for dynamic UI updates
    private Label weatherLabel;
    private Label weatherIcon;
    private Label moodLabel;
    private Label moodIcon;
    
    private Button createButton;
    private Button editButton;
    private Button viewButton;
    private Button saveButton;
    
    private String selectedDate;
    private Map<String, String> journalEntries;
    
    private Map<String, String> sentimentlabel;
    private Map<String, String> sentimentscore;
    private Map<String, String> weatherCache; 

    public JournalPage(App app, ApiClient api) {
        this.app = app;
        this.api = api;
        this.selectedDate = LocalDate.now().toString();
        this.journalEntries = new HashMap<>();
        this.sentimentlabel = new HashMap<>();
        this.sentimentscore = new HashMap<>();
        this.weatherCache = new HashMap<>();
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("magical-gradient");
        
        root.setTop(createTopBar());
        
        HBox mainContent = new HBox(0);
        mainContent.setAlignment(Pos.TOP_LEFT);
        
        VBox sidebar = createDateSidebar(); 
        entryView = createEntryView();
        
        mainContent.getChildren().addAll(sidebar, entryView);
        HBox.setHgrow(entryView, Priority.ALWAYS);
        
        root.setCenter(mainContent);
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
        backButton.setOnAction(e ->
        app.showWelcome(
            app.getCurrentUserEmail(),
            app.getCurrentUserDisplayName()
            )
        );

        
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
        
        Text sidebarTitle = new Text("Select Date");
        sidebarTitle.setStyle("-fx-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Date Picker for selecting any past date
        datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("date-picker");
        datePicker.setPrefWidth(160);
        // Disable future dates
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isAfter(LocalDate.now()));
            }
        });
        
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadEntry(newVal.toString());
            }
        });
        
        // Quick access to last 7 days
        Text quickAccessTitle = new Text("Last 7 Days");
        quickAccessTitle.setStyle("-fx-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-opacity: 0.8;");
        
        dateListView = new ListView<>();
        dateListView.getStyleClass().add("date-list");
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(i == 0 ? "Today" : date.format(formatter));
        }
        
        dateListView.getItems().addAll(dates);
        dateListView.getSelectionModel().select(0);
        
        dateListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String dateKey = newVal.equals("Today") ? LocalDate.now().toString() : newVal;
                datePicker.setValue(LocalDate.parse(dateKey));
                loadEntry(dateKey);
            }
        });
        
        VBox.setVgrow(dateListView, Priority.ALWAYS);
        sidebar.getChildren().addAll(sidebarTitle, datePicker, quickAccessTitle, dateListView);
        
        return sidebar;
    }

    private VBox createEntryView() {
        VBox view = new VBox(20);
        view.getStyleClass().add("entry-view");
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);
        
        HBox smartFeatures = new HBox(30);
        smartFeatures.setAlignment(Pos.CENTER);
        
        // Weather widget initialization
        VBox weatherWidget = new VBox(5);
        weatherWidget.getStyleClass().add("smart-widget");
        weatherWidget.setAlignment(Pos.CENTER);
        weatherIcon = new Label("☀"); 
        weatherIcon.setStyle("-fx-font-size: 32px; -fx-font-family: 'Segoe UI Emoji';");
        weatherLabel = new Label("Loading weather...");
        weatherLabel.setStyle("-fx-text-fill: #6a4c93; -fx-font-weight: bold;");
        weatherWidget.getChildren().addAll(weatherIcon, weatherLabel);
        
        // Mood widget initialization
        VBox moodWidget = new VBox(5);
        moodWidget.getStyleClass().add("smart-widget");
        moodWidget.setAlignment(Pos.CENTER);
        moodWidget.setMinWidth(150); 
        moodIcon = new Label("😊");
        moodIcon.setStyle("-fx-font-size: 32px;");
        moodLabel = new Label("Analyzing Mood...");
        moodLabel.setStyle("-fx-text-fill: #6a4c93; -fx-font-weight: bold;");
        moodWidget.getChildren().addAll(moodIcon, moodLabel);
        
        smartFeatures.getChildren().addAll(weatherWidget, moodWidget);
        
        VBox entryCard = new VBox(15);
        entryCard.getStyleClass().add("entry-card");
        entryCard.setAlignment(Pos.TOP_CENTER);
        entryCard.setPadding(new Insets(25));
        
        entryTextArea = new TextArea();
        entryTextArea.getStyleClass().add("entry-textarea");
        entryTextArea.setPromptText("Write your thoughts here...");
        entryTextArea.setPrefRowCount(12);
        entryTextArea.setWrapText(true);
        entryTextArea.setEditable(false);
        VBox.setVgrow(entryTextArea, Priority.ALWAYS);
        
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        
        createButton = new Button("Create Journal");
        createButton.getStyleClass().add("button-primary");
        editButton = new Button("Edit Journal");
        editButton.getStyleClass().add("button-primary");
        viewButton = new Button("View Journal");
        viewButton.getStyleClass().add("button-secondary");
        saveButton = new Button("Save");
        saveButton.getStyleClass().add("button-primary");
        
        createButton.setOnAction(e -> enableEditMode());
        editButton.setOnAction(e -> enableEditMode());
        viewButton.setOnAction(e -> disableEditMode());
        saveButton.setOnAction(e -> saveEntry());
        
        buttonBar.getChildren().addAll(createButton, editButton, viewButton, saveButton);
        entryCard.getChildren().addAll(new Text("Journal Entry"), entryTextArea, buttonBar);
        view.getChildren().addAll(smartFeatures, entryCard);
        
        return view;
    }

    private void loadEntry(String dateKey) {
        selectedDate = dateKey;
        String entry = journalEntries.get(dateKey);
        String email = app.getCurrentUserEmail();
        if (email == null || email.isBlank()) {
            System.err.println("No logged-in user");
            return;
        }

        boolean contentFetched = false;
        try {
            String resp = api.getJournalByDate(email, dateKey);
            if (resp != null && resp.contains("\"content\":\"")) {
                int start = resp.indexOf("\"content\":\"") + 11;
                int end = resp.indexOf("\"", start);
                entry = resp.substring(start, end).replace("\\\"", "\"");
                journalEntries.put(dateKey, entry);
                contentFetched = true;
            }
        } catch (Exception e) {
            System.err.println("Load entry failed: " + e.getMessage());
        }
        
        if (!contentFetched) {
            sentimentlabel.remove(dateKey);
            sentimentscore.remove(dateKey);
        }

        boolean isSentimentCached = sentimentlabel.containsKey(dateKey) && sentimentscore.containsKey(dateKey);
        if (contentFetched && !isSentimentCached) {
            try {
                String sentimentResp = api.getSentimentAnalysis(email, dateKey);
                if (sentimentResp != null && !sentimentResp.startsWith("Error")) {
                    parseAndCacheSentiment(dateKey, sentimentResp);
                }
            } catch (Exception e) {
                System.err.println("Sentiment API error: " + e.getMessage());
            }
        }
        
        boolean isToday = dateKey.equals(LocalDate.now().toString());
        entryTextArea.setText(entry != null ? entry : "");
        
        updateButtonVisibility(entry, isToday);
        updateSmartFeatures(dateKey);
    }

    private void updateButtonVisibility(String entry, boolean isToday) {
        boolean hasContent = entry != null && !entry.isEmpty();
        
        // Show create button only for today with empty entry
        createButton.setVisible(!hasContent && isToday);
        createButton.setManaged(!hasContent && isToday);
        
        // Show edit button for past dates or today with content
        editButton.setVisible(hasContent || !isToday);
        editButton.setManaged(hasContent || !isToday);
        
        // View button hidden since entries can be edited
        viewButton.setVisible(false);
        viewButton.setManaged(false);
        
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        
        // Ensure text area is not editable by default
        entryTextArea.setEditable(false);
    }

    private void enableEditMode() {
        entryTextArea.setEditable(true);
        createButton.setVisible(false); createButton.setManaged(false);
        editButton.setVisible(false); editButton.setManaged(false);
        viewButton.setVisible(false); viewButton.setManaged(false);
        saveButton.setVisible(true); saveButton.setManaged(true);
    }

    private void disableEditMode() {
        entryTextArea.setEditable(false);
        loadEntry(selectedDate);
    }

    private void saveEntry() {
        String content = entryTextArea.getText();
        String email = app.getCurrentUserEmail();
        if (email == null || email.isBlank() || email.equals("Guest")) email = "guest@example.com";

        try {
            journalEntries.put(selectedDate, content);
            
            // 1. Save journal for the selected date (works for any date, not just today)
            api.updateJournalForDate(email, selectedDate, content);
            
            // 2. TRIGGER WEEKLY UPDATE: This calls the service logic above
            // This ensures 'journal_weekly_sentiment' is now in sync with your edit
            api.computeWeeklySentiment(email); 
            
            // 3. Update single-day mood display
            String sentimentResp = api.getSentimentAnalysis(email, selectedDate);
            if (sentimentResp != null && !sentimentResp.startsWith("Error")) {
                parseAndCacheSentiment(selectedDate, sentimentResp);
            }
            
            disableEditMode();
        } catch (Exception e) {
            System.err.println("Auto-update failed: " + e.getMessage());
        }
    }
    
    private void parseAndCacheSentiment(String dateKey, String jsonResp) {
        String label = extractJsonStringValue(jsonResp, "sentimentlabel");
        String scoreRaw = extractJsonNumberValue(jsonResp, "sentimentscore");
        
        try {
            double val = Double.parseDouble(scoreRaw);
            sentimentscore.put(dateKey, String.format("%.0f%%", val * 100));
        } catch (Exception e) {
            sentimentscore.put(dateKey, "N/A");
        }
        sentimentlabel.put(dateKey, label);
    }

    private void updateSmartFeatures(String dateKey) {
        // 1. Weather Logic: Fetch from database by specific date
        if (weatherCache.containsKey(dateKey)) {
            String[] cached = weatherCache.get(dateKey).split("\\|");
            weatherLabel.setText(cached[0]); 
            weatherIcon.setText(convertWeatherIcon(cached[1]));
        } else {
            try {
                // Fetching from your new /weather/by-date endpoint
                String weatherJson = api.getWeatherByDate("Kuala Lumpur", dateKey); 
                
                String summary = extractJsonStringValue(weatherJson, "summaryForecast");
                String min = extractJsonNumberValue(weatherJson, "minTemp");
                String max = extractJsonNumberValue(weatherJson, "maxTemp");

                if (!min.equals("N/A") && !max.equals("N/A")) {
                    String tempRange = min + "°C - " + max + "°C";
                    weatherLabel.setText(tempRange);
                    weatherIcon.setText(convertWeatherIcon(summary));
                    weatherCache.put(dateKey, tempRange + "|" + summary);
                }
            } catch (Exception e) {
                weatherLabel.setText("Weather N/A");
                weatherIcon.setText("🌤");
            }
        }

        // 2. Mood Logic: Update UI with parsed sentiment
        String label = sentimentlabel.getOrDefault(dateKey, "N/A"); 
        String score = sentimentscore.getOrDefault(dateKey, "");
        
        if (label.equals("N/A") || score.isEmpty()) {
            moodLabel.setText("Analyzing Mood...");
            moodIcon.setText("😶");
        } else {
            moodLabel.setText(label + " - " + score);
            updateMoodIcon(label);
        }
    }

    private void updateMoodIcon(String label) {
        switch (label.toUpperCase()) {
            case "POSITIVE" -> moodIcon.setText("😊");
            case "NEGATIVE" -> moodIcon.setText("😟");
            case "NEUTRAL"  -> moodIcon.setText("😐");
            default          -> moodIcon.setText("😶");
        }
    }

    private String convertWeatherIcon(String backendSummary) {
        if (backendSummary == null) return "🌤";
        String s = backendSummary.toLowerCase();
        if (s.contains("tiada hujan") || s.contains("clear")) return "☀";
        if (s.contains("mendung") || s.contains("overcast")) return "☁";
        if (s.contains("hujan di beberapa tempat") || s.contains("drizzle")) return "🌧";
        if (s.contains("ribut petir")) return "⛈";
        return "🌤";
    }

    // Crude JSON Helpers
    private String extractJsonStringValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int idx = json.indexOf(searchKey);
        if (idx == -1) return "N/A";
        int start = json.indexOf(":", idx) + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = json.indexOf("\"", start);
        return (end != -1) ? json.substring(start, end).trim() : "N/A";
    }

    private String extractJsonNumberValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int idx = json.indexOf(searchKey);
        if (idx == -1) return "N/A";
        int start = json.indexOf(":", idx) + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) end++;
        return (start != end) ? json.substring(start, end) : "N/A";
    }
}