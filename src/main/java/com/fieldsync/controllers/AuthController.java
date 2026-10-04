package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.UserDAO;
import com.fieldsync.model.User;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    @FXML
    private Button btnRoleStudent;
    @FXML
    private Button btnRoleRegistrar;
    @FXML
    private Label lblHeroSubtitle;
    @FXML
    private Label lblFormTitle;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnSubmit;
    @FXML
    private Hyperlink linkToggleMode;

    private final UserDAO userDAO = new UserDAO();
    private boolean isRegistrarRole = false;
    private boolean isLoginMode = true;

    @FXML
    public void initialize() {
        updateRoleUI();
    }

    @FXML
    private void handleRoleStudent() {
        isRegistrarRole = false;
        updateRoleUI();
    }

    @FXML
    private void handleRoleRegistrar() {
        isRegistrarRole = true;
        updateRoleUI();
    }

    @FXML
    private void handleToggleMode() {
        isLoginMode = !isLoginMode;
        lblFormTitle.setText(isLoginMode ? "ONLINE MEMBER LOGIN" : "CREATE NEW ACCOUNT");
        btnSubmit.setText(isLoginMode ? "LOGIN" : "REGISTER");
        linkToggleMode.setText(isLoginMode ? "Register here!" : "Login here!");
        lblStatus.setText("");
    }

    // @FXML
    // private void handleSubmit() {
    // String email = txtEmail.getText().trim();
    // String password = txtPassword.getText().trim();

    // if (email.isEmpty() || password.isEmpty()) {
    // lblStatus.setText("Please enter both email and password.");
    // return;
    // }

    // if (isLoginMode) {
    // User user = userDAO.authenticate(email, password);
    // if (user != null) {
    // UserSession.login(user);
    // // Route based on role
    // if (user.isRegistrar()) {
    // SceneManager.switchScene(AppConfig.VIEW_REGISTRAR_DASHBOARD,
    // AppConfig.STYLE_DASHBOARD);
    // } else {
    // SceneManager.switchScene(AppConfig.VIEW_STUDENT_DASHBOARD,
    // AppConfig.STYLE_DASHBOARD);
    // }
    // } else {
    // lblStatus.setText("Invalid credentials or account does not exist.");
    // }
    // } else {
    // lblStatus.setText("Registration workflow connected to DB!");
    // }
    // }

    @FXML
    private void handleSubmit() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both email and password.");
            return;
        }

        if (isLoginMode) {
            try {
                System.out.println("[Auth] Attempting login for: " + email);
                User user = userDAO.authenticate(email, password);

                // if (user != null) {
                // System.out.println("[Auth] User authenticated: " + user.getEmail() + " |
                // Role: " + user.getRole());
                // UserSession.login(user);

                // // Route based on role stored in DB
                // String targetView = "REGISTRAR".equalsIgnoreCase(user.getRole())
                // ? AppConfig.VIEW_REGISTRAR_DASHBOARD
                // : AppConfig.VIEW_STUDENT_DASHBOARD;

                // System.out.println("[Auth] Switching scene to: " + targetView);
                // SceneManager.switchScene(targetView, AppConfig.STYLE_DASHBOARD);
                // } else {
                // lblStatus.setText("Invalid credentials or database connection failed.");
                // }
                if (user != null) {
                    UserSession.login(user);

                    System.out.println("[Auth] Authenticated Role: " + user.getRole());

                    if (user.isRegistrar()) {
                        System.out.println("[Auth] Routing to Registrar Dashboard...");
                        SceneManager.switchScene(AppConfig.VIEW_REGISTRAR_DASHBOARD, AppConfig.STYLE_DASHBOARD);
                    } else {
                        System.out.println("[Auth] Routing to Student Rep Dashboard...");
                        SceneManager.switchScene(AppConfig.VIEW_STUDENT_DASHBOARD, AppConfig.STYLE_DASHBOARD);
                    }
                } else {
                    lblStatus.setText("Invalid credentials or database connection failed.");
                }

            } catch (Exception e) {
                System.err.println("[Auth] Critical error during login:");
                e.printStackTrace();
                lblStatus.setText("An unexpected error occurred during login.");
            }
        } else {
            lblStatus.setText("Registration workflow connected to DB!");
        }
    }

    private void updateRoleUI() {
        if (isRegistrarRole) {
            btnRoleRegistrar.getStyleClass().add("role-tab-active");
            btnRoleStudent.getStyleClass().remove("role-tab-active");
            lblHeroSubtitle.setText("CAMPUS REGISTRAR & VENUE MANAGEMENT");
        } else {
            btnRoleStudent.getStyleClass().add("role-tab-active");
            btnRoleRegistrar.getStyleClass().remove("role-tab-active");
            lblHeroSubtitle.setText("STUDENT REPRESENTATIVE ATHLETICS HUB");
        }
    }
}