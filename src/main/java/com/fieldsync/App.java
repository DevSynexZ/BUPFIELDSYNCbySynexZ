package com.fieldsync;

import com.fieldsync.config.AppConfig;
import com.fieldsync.navigation.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.init(primaryStage);
        // Launch Authentication View as initial screen
        SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
    }

    public static void main(String[] args) {
        launch(args);
    }
}