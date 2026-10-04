package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;
import javafx.fxml.FXML;

public class RegistrarDashboardController {

    @FXML
    public void initialize() {
        // Load initial registrar analytics
    }

    @FXML
    private void handleLogout() {
        UserSession.logout();
        SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
    }
}