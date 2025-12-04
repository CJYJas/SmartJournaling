package com.example.smartjournaling.frontend;

import com.example.smartjournaling.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public JournalPage(App app, ApiClient api) {
        this.app = app;
        this.api = api;
        this.selectedDate = LocalDate.now().toString();
        this.journalEntries = new HashMap<>();
        
        // Mock data for demonstration
        journalEntries.put("2025-01-08", "Today was a productive day. I completed my project and felt accomplished.");
        journalEntries.put("2025-01-09", "Felt a bit anxious about the upcoming deadline, but stayed positive.");
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
        
        // Back button
        Button backButton = new Button("← Back to Home");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> app.showWelcome(app.getCurrentUserName()));
        
        // Page title
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
        
        // Populate with dates
        List<String> dates = new ArrayList<>();
        dates.add("Today");
        dates.add("2025-01-09");
        dates.add("2025-01-08");
        dates.add("2025-01-07");
        dates.add("2025-01-06");
        
        dateListView.getItems().addAll(dates);
        dateListView.getSelectionModel().select(0);
        
        dateListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
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
        weatherLabel = new Label("Thunderstorms, 24°C");
        weatherLabel.setStyle("-fx-text-fill: #6a4c93; -fx-font-weight: bold;");
        weatherWidget.getChildren().addAll(weatherIcon, weatherLabel);
        
        // Mood analysis widget
        VBox moodWidget = new VBox(5);
        moodWidget.getStyleClass().add("smart-widget");
        moodWidget.setAlignment(Pos.CENTER);
        Label moodIcon = new Label("😊");
        moodIcon.setStyle("-fx-font-size: 32px;");
        moodLabel = new Label("Positive - 99%");
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
        // Try to fetch from backend if available
        try {
            String email = app.getCurrentUserName();
            if (email == null || email.isBlank() || email.equals("Guest")) {
                email = "guest@example.com"; // fallback email if none set
            }
            String resp = api.getJournalByDate(email, dateKey);
            if (resp != null && !resp.isBlank() && resp.contains("content")) {
                // crude JSON extraction of content field
                int idx = resp.indexOf("\"content\"");
                if (idx != -1) {
                    int colon = resp.indexOf(":", idx);
                    int start = resp.indexOf("\"", colon + 1) + 1;
                    int end = resp.indexOf("\"", start);
                    if (start > 0 && end > start) {
                        entry = resp.substring(start, end);
                        journalEntries.put(dateKey, entry);
                    }
                }
            }
        } catch (Exception e) {
            // ignore and use local mock
        }
        boolean isToday = dateKey.equals(LocalDate.now().toString());
        
        if (entry == null || entry.isEmpty()) {
            // Empty entry
            entryTextArea.setText("");
            if (isToday) {
                // Show create button
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
                // Past date with no entry
                hideAllButtons();
                entryTextArea.setEditable(false);
            }
        } else {
            // Has entry
            entryTextArea.setText(entry);
            if (isToday) {
                // Show edit and view buttons
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
                // Past date - read only
                hideAllButtons();
                entryTextArea.setEditable(false);
            }
        }
        
        // Update weather and mood (mock data)
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
        
        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Journal entry saved successfully!");
        alert.showAndWait();
        
        // Switch back to view mode
        disableEditMode();
        
        // Reload to update buttons
        // Save to backend as well
        try {
            String email = app.getCurrentUserName();
            if (email == null || email.isBlank() || email.equals("Guest")) {
                email = "guest@example.com";
            }
            String resp = api.addOrEditTodayJson(email, selectedDate, content);
            if (resp != null && !resp.isBlank()) {
                System.out.println("Backend response: " + resp);
            }
        } catch (Exception e) {
            // ignore for now
        }
        loadEntry(selectedDate);
    }

    private void updateSmartFeatures(String dateKey) {
        // Mock weather data
        if (dateKey.equals(LocalDate.now().toString())) {
            weatherLabel.setText("Thunderstorms, 24°C");
        } else {
            weatherLabel.setText("Partly Cloudy, 26°C");
        }
        
        // Mock mood analysis
        String entry = journalEntries.get(dateKey);
        if (entry != null && entry.contains("anxious")) {
            moodLabel.setText("Mixed - 65%");
        } else if (entry != null && entry.contains("productive")) {
            moodLabel.setText("Positive - 95%");
        } else {
            moodLabel.setText("Neutral - 75%");
        }
    }
}
