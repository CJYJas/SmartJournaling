package com.example.smartjournaling.frontend;

import com.example.smartjournaling.App;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Main {
    private final App app;
    private final VBox content;

    public Main(App app) {
        this.app = app;
        this.content = new VBox(25);
        setupUI();
    }

    private void setupUI() {
        content.setAlignment(Pos.CENTER);

        Text title = new Text("Smart Journal");
        title.getStyleClass().add("header-title");

        Text subtitle = new Text("Reflect. Write. Grow.");
        subtitle.getStyleClass().add("header-subtitle");

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setOnAction(e -> app.showLogin());

        Button signupBtn = new Button("Sign Up");
        signupBtn.getStyleClass().add("button-secondary");
        signupBtn.setOnAction(e -> app.showSignup());

        buttons.getChildren().addAll(loginBtn, signupBtn);
        
        Region spacer = new Region();
        spacer.setMinHeight(30);

        content.getChildren().addAll(title, subtitle, spacer, buttons);
    }

    public Parent getView() {
        StackPane root = new StackPane();
        root.getStyleClass().add("magical-gradient");
        root.getChildren().add(content);
        return root;
    }

    public void animateEntry() {
        content.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1200), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}