package com.fieldsync.navigation;

import com.fieldsync.config.AppConfig;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle(AppConfig.APP_TITLE);
        primaryStage.setMinWidth(AppConfig.MIN_WIDTH);
        primaryStage.setMinHeight(AppConfig.MIN_HEIGHT);
    }

    public static void switchScene(String fxmlPath, String specificStylePath) {
        try {
            URL fxmlUrl = SceneManager.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root, AppConfig.MIN_WIDTH, AppConfig.MIN_HEIGHT);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            // Apply Global Styles and Scene Specific Stylesheet
            scene.getStylesheets().clear();
            URL mainCss = SceneManager.class.getResource(AppConfig.STYLE_MAIN);
            if (mainCss != null) scene.getStylesheets().add(mainCss.toExternalForm());

            if (specificStylePath != null) {
                URL specificCss = SceneManager.class.getResource(specificStylePath);
                if (specificCss != null) scene.getStylesheets().add(specificCss.toExternalForm());
            }

            // Smooth Fade Transition
            FadeTransition ft = new FadeTransition(Duration.millis(300), root);
            ft.setFromValue(0.2);
            ft.setToValue(1.0);
            ft.play();

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        } catch (IOException e) {
            System.err.println("[SceneManager] Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}