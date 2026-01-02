package com.example.smartjournaling.frontend;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.example.smartjournaling.App;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Welcome {
    private final App app;
    private final ApiClient api;
    
    private Label weatherStatus;
    private Label weatherLocation; // Updated to be dynamic
    private Label timeLabel;
    private Label userNameLabel;
    private Text greetingTitle;
    private Text greetingName;
    
    private String displayName = "Guest";

    public Welcome(App app, ApiClient api) {
        this.app = app;
        this.api = api;
        initializeComponents();
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        if (userNameLabel != null) userNameLabel.setText(name);
        if (greetingName != null) greetingName.setText(name);
        if (greetingTitle != null) greetingTitle.setText(getGreetingBasedOnTime() + ",");
    }

    private void initializeComponents() {
        weatherStatus = new Label("Loading...");
        weatherStatus.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        
        weatherLocation = new Label("Fetching..."); // Default placeholder
        weatherLocation.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");

        timeLabel = new Label();
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Arial';");
        
        userNameLabel = new Label(displayName);
        userNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        greetingTitle = new Text();
        greetingTitle.getStyleClass().add("header-title");
        
        greetingName = new Text(displayName);
        greetingName.setStyle("-fx-font-size: 48px; -fx-fill: white; -fx-font-weight: bold;");
        
        startClock();
    }

    public void refreshData() {
        greetingTitle.setText(getGreetingBasedOnTime() + ",");
        
        // Fetch Weather details from database
       new Thread(() -> {
        try {
            String jsonResponse = api.getLatestWeatherData("Kuala Lumpur"); 
            
            // Match the field name in your Java Model
            String location = extractJsonValue(jsonResponse, "locationName"); 
            String summary = extractJsonValue(jsonResponse, "summaryForecast");

            Platform.runLater(() -> {
                if (!location.equals("N/A")) weatherLocation.setText(location);
                if (!summary.equals("N/A")) weatherStatus.setText(summary);
            });
        } catch (Exception e) {
            Platform.runLater(() -> weatherLocation.setText("Error"));
        }
    }).start();
        
    }

    /**
     * Improved helper to find specific keys in the JSON response
     */
    private String extractJsonValue(String json, String key) {
        if (json == null || !json.contains("\"" + key + "\"")) return "N/A";
        try {
            // Find the key, then the colon, then the first quote of the value
            int keyIdx = json.indexOf("\"" + key + "\"");
            int colonIdx = json.indexOf(":", keyIdx);
            int valueStart = json.indexOf("\"", colonIdx) + 1;
            int valueEnd = json.indexOf("\"", valueStart);
            
            return json.substring(valueStart, valueEnd);
        } catch (Exception e) {
            return "N/A";
        }
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("magical-gradient");

        HBox topBar = new HBox(20);
        topBar.getStyleClass().add("weather-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label weatherIcon = new Label("☁");
        weatherIcon.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        
        VBox weatherBox = new VBox(2);
        // Uses the dynamic weatherLocation field instead of hardcoded text
        weatherBox.getChildren().addAll(weatherLocation, weatherStatus);

        HBox weatherSection = new HBox(10, weatherIcon, weatherBox);
        weatherSection.setAlignment(Pos.CENTER_LEFT);

        HBox userSection = new HBox(10);
        userSection.setAlignment(Pos.CENTER_RIGHT);
        Circle userAvatar = new Circle(18, Color.WHITE);
        userAvatar.setStroke(Color.web("#ffffff", 0.5));
        userSection.getChildren().addAll(userNameLabel, userAvatar);
        
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topBar.getChildren().addAll(weatherSection, leftSpacer, timeLabel, rightSpacer, userSection);
        root.setTop(topBar);

        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        HBox menuGrid = new HBox(30);
        menuGrid.setAlignment(Pos.CENTER);
        VBox journalOption = createMenuCard("Journal", "Create, Edit & View", "Write today's story...");
        VBox summaryOption = createMenuCard("Summary", "Weekly Mood", "View your analytics...");
        menuGrid.getChildren().addAll(journalOption, summaryOption);

        content.getChildren().addAll(greetingTitle, greetingName, menuGrid);
        root.setCenter(content);

        return root;
    }

    private VBox createMenuCard(String title, String sub, String desc) {
        VBox card = new VBox(10);
        card.getStyleClass().add("menu-button");
        card.setPrefSize(220, 150);
        
        Text t = new Text(title);
        t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #6a4c93;");
        Text s = new Text(sub);
        s.setStyle("-fx-font-size: 14px; -fx-fill: #888; -fx-font-weight: bold;");
        Text d = new Text(desc);
        d.setStyle("-fx-font-size: 12px; -fx-fill: #aaa; -fx-font-style: italic;");
        
        card.setOnMouseClicked(e -> {
            if (title.equals("Journal")) app.showJournalPage();
            else if (title.equals("Summary")) app.showWeeklySummary();
        });
        
        card.getChildren().addAll(t, s, d);
        return card;
    }

    private void startClock() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private String getGreetingBasedOnTime() {
        int hour = LocalTime.now().getHour();
        if (hour >= 0 && hour < 12) return "Good Morning";
        if (hour >= 12 && hour < 17) return "Good Afternoon";
        return "Good Evening";
    }
}