package com.example.smartjournaling;

import com.example.smartjournaling.frontend.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private Stage stage;
    private final ApiClient api = new ApiClient();

    // Views (renamed classes)
    private Main mainView;
    private Login loginView;
    private Signup signupView;
    private Welcome welcomeView;
    private JournalPage journalPage;
    private WeeklySummaryPage weeklySummaryPage;
    
    private String currentUserName = "Guest";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Smart Journal");

        // Initialize Views with corrected class names
        mainView = new Main(this);
        loginView = new Login(this, api);
        signupView = new Signup(this, api);
        welcomeView = new Welcome(this, api);
        journalPage = new JournalPage(this, api);
        weeklySummaryPage = new WeeklySummaryPage(this, api);

        showMain();
        primaryStage.show();
    }

    // --- Navigation Methods ---

    public void showMain() {
        setScene(mainView.getView(), 800, 600);
        mainView.animateEntry();
    }

    public void showLogin() {
        setScene(loginView.getView(), 800, 600);
    }

    public void showSignup() {
        setScene(signupView.getView(), 800, 600);
    }

    public void showWelcome(String displayName) {
        currentUserName = displayName;
        welcomeView.setDisplayName(displayName);
        welcomeView.refreshData(); // Fetch weather/time
        setScene(welcomeView.getView(), 900, 650);
    }
    
    public void showJournalPage() {
        setScene(journalPage.getView(), 1000, 700);
    }
    
    public void showWeeklySummary() {
        setScene(weeklySummaryPage.getView(), 1000, 700);
    }
    
    public String getCurrentUserName() {
        return currentUserName;
    }

    private void setScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
        }
        stage.setScene(scene);
    }
}
