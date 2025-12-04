package com.example.smartjournaling.frontend;

import com.example.smartjournaling.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeeklySummaryPage {
    private final App app;
    private final ApiClient api;

    public WeeklySummaryPage(App app, ApiClient api) {
        this.app = app;
        this.api = api;
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
        
        // Stats cards
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        
        VBox highlightCard = createStatCard("Weekly Highlight", "Mostly Positive", "😊");
        VBox entriesCard = createStatCard("Entries", "5 of 7 days", "📝");
        VBox avgMoodCard = createStatCard("Avg Mood", "Positive - 87%", "💚");
        
        statsCards.getChildren().addAll(highlightCard, entriesCard, avgMoodCard);
        
        // Mood fluctuations timeline
        VBox moodSection = createMoodTimeline();
        
        // Weather history
        VBox weatherSection = createWeatherHistory();
        
        mainContent.getChildren().addAll(titleSection, statsCards, moodSection, weatherSection);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        // Bind content width to the ScrollPane viewport so children lay out to available width
        mainContent.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
        
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
        
        // Date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
        Text dateRange = new Text(startDate.format(formatter) + " - " + endDate.format(formatter));
        dateRange.setStyle("-fx-fill: white; -fx-font-size: 14px; -fx-opacity: 0.8;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(backButton, title, spacer, dateRange);
        return topBar;
    }

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
        cardValue.setStyle("-fx-fill: #6a4c93; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(icon, cardTitle, cardValue);
        return card;
    }

    private VBox createMoodTimeline() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        Text sectionTitle = new Text("Mood Fluctuations");
        sectionTitle.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Timeline container
        HBox timeline = new HBox(15);
        timeline.setAlignment(Pos.CENTER);
        timeline.getStyleClass().add("timeline-container");
        
        // Mock mood data for the past 7 days
        List<MoodDay> moodData = new ArrayList<>();
        moodData.add(new MoodDay("Mon", "Happy", "😊", "#95e1d3"));
        moodData.add(new MoodDay("Tue", "Stressed", "😰", "#f38181"));
        moodData.add(new MoodDay("Wed", "Calm", "😌", "#a8e6cf"));
        moodData.add(new MoodDay("Thu", "Anxious", "😟", "#ffd3b6"));
        moodData.add(new MoodDay("Fri", "Excited", "🤗", "#ffaaa5"));
        moodData.add(new MoodDay("Sat", "Peaceful", "😊", "#95e1d3"));
        moodData.add(new MoodDay("Sun", "Content", "😌", "#a8e6cf"));
        
        for (MoodDay day : moodData) {
            VBox dayCard = createMoodDayCard(day);
            timeline.getChildren().add(dayCard);
        }
        
        section.getChildren().addAll(sectionTitle, timeline);
        return section;
    }

    private VBox createMoodDayCard(MoodDay day) {
        VBox card = new VBox(8);
        card.getStyleClass().add("mood-day-card");
        card.setPrefSize(100, 120);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + day.color + "; -fx-background-radius: 12; -fx-padding: 12;");
        
        Text dayLabel = new Text(day.dayName);
        dayLabel.setStyle("-fx-fill: #333; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Label emoji = new Label(day.emoji);
        emoji.setStyle("-fx-font-size: 32px;");
        
        Text mood = new Text(day.mood);
        mood.setStyle("-fx-fill: #555; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(dayLabel, emoji, mood);
        return card;
    }

    private VBox createWeatherHistory() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        Text sectionTitle = new Text("Weather History");
        sectionTitle.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Weather container
        HBox weatherGrid = new HBox(15);
        weatherGrid.setAlignment(Pos.CENTER);
        
        // Mock weather data
        weatherGrid.getChildren().addAll(
            createWeatherCard("Mon", "☀️", "28°C"),
            createWeatherCard("Tue", "⛈️", "24°C"),
            createWeatherCard("Wed", "🌤️", "26°C"),
            createWeatherCard("Thu", "☁️", "25°C"),
            createWeatherCard("Fri", "🌧️", "23°C"),
            createWeatherCard("Sat", "🌤️", "27°C"),
            createWeatherCard("Sun", "☀️", "29°C")
        );
        
        section.getChildren().addAll(sectionTitle, weatherGrid);
        return section;
    }

    private VBox createWeatherCard(String day, String icon, String temp) {
        VBox card = new VBox(8);
        card.getStyleClass().add("weather-card");
        card.setPrefSize(90, 100);
        card.setAlignment(Pos.CENTER);
        
        Text dayLabel = new Text(day);
        dayLabel.setStyle("-fx-fill: #6a4c93; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        Label weatherIcon = new Label(icon);
        weatherIcon.setStyle("-fx-font-size: 28px;");
        
        Text temperature = new Text(temp);
        temperature.setStyle("-fx-fill: #888; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(dayLabel, weatherIcon, temperature);
        return card;
    }

    // Helper class for mood data
    private static class MoodDay {
        String dayName;
        String mood;
        String emoji;
        String color;
        
        MoodDay(String dayName, String mood, String emoji, String color) {
            this.dayName = dayName;
            this.mood = mood;
            this.emoji = emoji;
            this.color = color;
        }
    }
}

