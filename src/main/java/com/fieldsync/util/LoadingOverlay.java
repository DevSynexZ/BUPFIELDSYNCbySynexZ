package com.fieldsync.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingOverlay {

    private static final String[] FOOTBALL_MESSAGES = {
        "⚽ Referee checking VAR on the database...",
        "🏃 Strikers warming up, fetching your data...",
        "🟢 Pitch is clear, connecting to cloud server...",
        "⏱️ Adding 3 minutes of stoppage time...",
        "🎯 Passing data through the midfield..."
    };

    private static int messageIndex = 0;

    public static StackPane createOverlay(StackPane rootContainer) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(10, 15, 25, 0.85);"); // Tactical dark theme
        overlay.setAlignment(Pos.CENTER);

        // Animated pulsing football representation
        Circle ballGlow = new Circle(35);
        ballGlow.setFill(Color.web("#10B981")); // Neon pitch green
        ballGlow.setOpacity(0.2);

        Circle ball = new Circle(25);
        ball.setFill(Color.web("#1F2937"));
        ball.setStroke(Color.web("#10B981"));
        ball.setStrokeWidth(3);

        Label titleLabel = new Label("FIELDSYNC ENGINE");
        titleLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");

        Label statusLabel = new Label(FOOTBALL_MESSAGES[0]);
        statusLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");

        // Cycle through fun commentary messages while loading
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            messageIndex = (messageIndex + 1) % FOOTBALL_MESSAGES.length;
            statusLabel.setText(FOOTBALL_MESSAGES[messageIndex]);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Stop timeline when overlay is removed
        overlay.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                timeline.stop();
            }
        });

        VBox contentBox = new VBox(15, new StackPane(ballGlow, ball), titleLabel, statusLabel);
        contentBox.setAlignment(Pos.CENTER);

        overlay.getChildren().add(contentBox);
        return overlay;
    }
}