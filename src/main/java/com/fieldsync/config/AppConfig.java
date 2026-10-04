package com.fieldsync.config;

public class AppConfig {
    public static final String APP_TITLE = "FieldSync - Campus Field Reservation System";
    public static final double MIN_WIDTH = 1100;
    public static final double MIN_HEIGHT = 720;
    
    // Resource Paths
    public static final String FXML_PATH = "/fxml/";
    public static final String STYLES_PATH = "/styles/";
    
    // Views
    public static final String VIEW_AUTH = FXML_PATH + "AuthView.fxml";
    public static final String VIEW_STUDENT_DASHBOARD = FXML_PATH + "UserRepDashboard.fxml";
    public static final String VIEW_REGISTRAR_DASHBOARD = FXML_PATH + "RegistrarDashboard.fxml";
    
    // Stylesheets
    public static final String STYLE_MAIN = STYLES_PATH + "main.css";
    public static final String STYLE_AUTH = STYLES_PATH + "auth.css";
    public static final String STYLE_DASHBOARD = STYLES_PATH + "dashboard.css";
}