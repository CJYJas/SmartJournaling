package com.example.smartjournaling.frontend;

import com.example.smartjournaling.App;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Login {
    private final App app;
    private final ApiClient api;

    public Login(App app, ApiClient api) {
        this.app = app;
        this.api = api;
    }

    public Parent getView() {
        StackPane root = new StackPane();
        root.getStyleClass().add("magical-gradient");

        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setMaxWidth(350);
        card.setMaxHeight(420);
        card.setAlignment(Pos.CENTER);

        Text title = new Text("Welcome Back!");
        title.getStyleClass().add("card-title");
        
        Text sub = new Text("Sign in to your journal");
        sub.getStyleClass().add("card-description");

        TextField emailField = new TextField();
        emailField.setPromptText("you@example.com");
        emailField.getStyleClass().add("text-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.getStyleClass().add("password-field");

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);
        statusLabel.setWrapText(true);

        Button signInBtn = new Button("Sign In");
        signInBtn.setMaxWidth(Double.MAX_VALUE);
        signInBtn.getStyleClass().add("button-primary");
        
        signInBtn.setOnAction(e -> {
            if (emailField.getText().isEmpty() || passField.getText().isEmpty()) return;
            
            statusLabel.setText("Signing in...");
            signInBtn.setDisable(true);

            new Thread(() -> {
                String response = api.login(emailField.getText(), passField.getText());
                Platform.runLater(() -> {
                    signInBtn.setDisable(false);
                    if (response.contains("Error") || response.contains("not exist") || response.contains("Incorrect")) {
                        statusLabel.setText(response);
                        statusLabel.setTextFill(Color.RED);
                    } else {
                        // Expected format from backend: "Good Morning, [Name]"
                        String displayName = "User";
                        String[] parts = response.split(", ");
                        if(parts.length > 1) displayName = parts[1];
                        
                        app.showWelcome(displayName);
                    }
                });
            }).start();
        });

        Hyperlink signupLink = new Hyperlink("Don't have an account? Sign up");
        signupLink.setTextFill(Color.GRAY);
        signupLink.setOnAction(e -> app.showSignup());

        card.getChildren().addAll(
            title, sub, statusLabel, 
            new Label("Email"), emailField, 
            new Label("Password"), passField, 
            signInBtn, signupLink
        );
        
        root.getChildren().add(card);
        return root;
    }
}