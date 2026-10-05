
package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.UserDAO;
import com.fieldsync.model.User;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AuthController {

    @FXML private Button btnRoleStudent;
    @FXML private Button btnRoleRegistrar;
    @FXML private Label lblHeroSubtitle;
    @FXML private Label lblFormTitle;
    @FXML private TextField txtFullName;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblStatus;
    @FXML private Button btnSubmit;
    @FXML private Hyperlink linkToggleMode;
    @FXML private ImageView imgLeftBanner;

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

        boolean showRegFields = !isLoginMode && !isRegistrarRole;
        txtFullName.setVisible(showRegFields);
        txtFullName.setManaged(showRegFields);
        txtDepartment.setVisible(showRegFields);
        txtDepartment.setManaged(showRegFields);
    }

    @FXML
    private void handleSubmit() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblStatus.setStyle("-fx-text-fill: #ef4444;");
            lblStatus.setText("Please enter both email and password.");
            return;
        }

        if (isLoginMode) {
            handleLogin(email, password);
        } else {
            handleRegistration(email, password);
        }
    }

    private void handleLogin(String email, String password) {
        try {
            System.out.println("[Auth] Attempting login for: " + email);
            User user = userDAO.authenticate(email, password);

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
                lblStatus.setStyle("-fx-text-fill: #ef4444;");
                lblStatus.setText("Invalid credentials or database connection failed.");
            }
        } catch (Exception e) {
            System.err.println("[Auth] Critical error during login:");
            e.printStackTrace();
            lblStatus.setStyle("-fx-text-fill: #ef4444;");
            lblStatus.setText("An unexpected error occurred during login.");
        }
    }

    private void handleRegistration(String email, String password) {
        try {
            if (userDAO.emailExists(email)) {
                lblStatus.setStyle("-fx-text-fill: #ef4444;");
                lblStatus.setText("Email is already registered. Please login.");
                return;
            }

            String fullName = txtFullName.getText().trim();
            String department = txtDepartment.getText().trim();

            if (!isRegistrarRole) {
                if (fullName.isEmpty() || department.isEmpty()) {
                    lblStatus.setStyle("-fx-text-fill: #ef4444;");
                    lblStatus.setText("Please fill in Representative Name and Department.");
                    return;
                }
            } else {
                fullName = email.contains("@") ? email.split("@")[0] : email;
                department = "Administration";
            }

            String role = isRegistrarRole ? "REGISTRAR" : "STUDENT_REP";
            User newUser = new User(0, fullName, email, password, role, department);

            boolean isRegistered = userDAO.registerUser(newUser);

            if (isRegistered) {
                lblStatus.setStyle("-fx-text-fill: #22c55e;");
                lblStatus.setText("Account created! Logging in...");

                User authenticatedUser = userDAO.authenticate(email, password);
                if (authenticatedUser != null) {
                    UserSession.login(authenticatedUser);
                    if (authenticatedUser.isRegistrar()) {
                        SceneManager.switchScene(AppConfig.VIEW_REGISTRAR_DASHBOARD, AppConfig.STYLE_DASHBOARD);
                    } else {
                        SceneManager.switchScene(AppConfig.VIEW_STUDENT_DASHBOARD, AppConfig.STYLE_DASHBOARD);
                    }
                }
            } else {
                lblStatus.setStyle("-fx-text-fill: #ef4444;");
                lblStatus.setText("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("[Auth] Critical error during registration:");
            e.printStackTrace();
            lblStatus.setStyle("-fx-text-fill: #ef4444;");
            lblStatus.setText("An error occurred during registration.");
        }
    }

    private void updateRoleUI() {
        if (isRegistrarRole) {
            btnRoleRegistrar.getStyleClass().add("role-tab-active");
            btnRoleStudent.getStyleClass().remove("role-tab-active");
            lblHeroSubtitle.setText("CAMPUS REGISTRAR & VENUE MANAGEMENT");
            setBannerImage("/images/registrar.jpg");
        } else {
            btnRoleStudent.getStyleClass().add("role-tab-active");
            btnRoleRegistrar.getStyleClass().remove("role-tab-active");
            lblHeroSubtitle.setText("STUDENT REPRESENTATIVE ATHLETICS HUB");
            setBannerImage("/images/student_rep.jpg");
        }

        boolean showRegFields = !isLoginMode && !isRegistrarRole;
        if (txtFullName != null && txtDepartment != null) {
            txtFullName.setVisible(showRegFields);
            txtFullName.setManaged(showRegFields);
            txtDepartment.setVisible(showRegFields);
            txtDepartment.setManaged(showRegFields);
        }
    }

    // private void setBannerImage(String resourcePath) {
    //     try {
    //         if (getClass().getResource(resourcePath) != null) {
    //             imgLeftBanner.setImage(new Image(getClass().getResourceAsStream(resourcePath)));
    //         }
    //     } catch (Exception e) {
    //         System.err.println("[Auth] Could not load image: " + resourcePath);
    //     }
    // }

    private void setBannerImage(String resourcePath) {
    try {
        if (getClass().getResource(resourcePath) != null) {
            Image img = new Image(getClass().getResourceAsStream(resourcePath));
            imgLeftBanner.setImage(img);
        }
    } catch (Exception e) {
        System.err.println("[Auth] Could not load image: " + resourcePath);
    }
}
}