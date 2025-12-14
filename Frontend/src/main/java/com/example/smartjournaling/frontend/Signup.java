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
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class Signup {
    private final App app;
    private final ApiClient api;

    public Signup(App app, ApiClient api) {
        this.app = app;
        this.api = api;
    }

    public Parent getView() {
        StackPane root = new StackPane();
        root.getStyleClass().add("magical-gradient");

        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setMaxWidth(350);
        card.setAlignment(Pos.CENTER);

        Text title = new Text("Get Started");
        title.getStyleClass().add("card-title");
        
        Text sub = new Text("Create your journal account");
        sub.getStyleClass().add("card-description");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");

        TextField nameField = new TextField();
        nameField.setPromptText("Display Name");
        nameField.getStyleClass().add("text-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("password-field");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button createBtn = new Button("Create Account");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.getStyleClass().add("button-primary");

        createBtn.setOnAction(e -> {
            statusLabel.setText("Creating account...");
            statusLabel.setTextFill(Color.BLACK);
            createBtn.setDisable(true);
            
            new Thread(() -> {
                String response = api.signup(emailField.getText(), nameField.getText(), passField.getText());
                Platform.runLater(() -> {
                    createBtn.setDisable(false);
                    if (response.toLowerCase().contains("success")) {
                        statusLabel.setText("Success! Redirecting to login...");
                        statusLabel.setTextFill(Color.GREEN);
                        // Redirect after delay
                        new Timeline(new KeyFrame(Duration.seconds(1.5), ev -> app.showLogin())).play();
                    } else {
                        statusLabel.setText(response);
                        statusLabel.setTextFill(Color.RED);
                    }
                });
            }).start();
        });

        Hyperlink loginLink = new Hyperlink("Already have an account? Sign in");
        loginLink.setTextFill(Color.GRAY);
        loginLink.setOnAction(e -> app.showLogin());

        card.getChildren().addAll(
            title, sub, statusLabel, 
            new Label("Email"), emailField, 
            new Label("Display Name"), nameField, 
            new Label("Password"), passField, 
            createBtn, loginLink
        );

        root.getChildren().add(card);
        return root;
    }
}