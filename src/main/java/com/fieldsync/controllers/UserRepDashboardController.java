// package com.fieldsync.controllers;

// import com.fieldsync.config.AppConfig;
// import com.fieldsync.navigation.SceneManager;
// import com.fieldsync.util.UserSession;
// import javafx.fxml.FXML;

// public class UserRepDashboardController {

//     @FXML
//     public void initialize() {
//         // Load initial user representative data
//     }

//     @FXML
//     private void handleNewReservation() {
//         System.out.println("[Dashboard] Opening reservation modal...");
//     }

//     @FXML
//     private void handleLogout() {
//         UserSession.logout();
//         SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
//     }
// }

// package com.fieldsync.controllers;

// import com.fieldsync.config.AppConfig;
// import com.fieldsync.dao.ReservationDAO;
// import com.fieldsync.dao.ReservationDAO.ReservationDisplayDTO;
// import com.fieldsync.model.User;
// import com.fieldsync.navigation.SceneManager;
// import com.fieldsync.util.UserSession;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.fxml.FXML;
// import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.List;

// public class UserRepDashboardController {

//     @FXML private Label lblActiveReservations;
//     @FXML private Label lblNextMatch;
//     @FXML private Label lblPendingApprovals;
//     @FXML private Label lblRegisteredPlayers;

//     @FXML private TableView<ReservationDisplayDTO> tblReservations;
//     @FXML private TableColumn<ReservationDisplayDTO, String> colFieldName;
//     @FXML private TableColumn<ReservationDisplayDTO, String> colDate;
//     @FXML private TableColumn<ReservationDisplayDTO, String> colTime;
//     @FXML private TableColumn<ReservationDisplayDTO, String> colStatus;

//     private final ReservationDAO reservationDAO = new ReservationDAO();

//     @FXML
//     public void initialize() {
//         setupTableColumns();
//         loadDashboardData();
//     }

//     private void setupTableColumns() {
//         colFieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
//         colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
//         colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
//         colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
//         tblReservations.setPlaceholder(new Label("No upcoming field reservations found."));
//     }

//     public void loadDashboardData() {
//         User currentUser = UserSession.getLoggedInUser();
//         //debug
//         System.out.println("[DEBUG] Logged in User: " + (currentUser != null ? currentUser.getEmail() + " (ID: " + currentUser.getId() + ")" : "NULL"));
//         if (currentUser == null) {
//             System.err.println("[Dashboard] No user currently in session!");
//             return;
//         }

//         int userId = currentUser.getUserId();

//         //debug
//         List<ReservationDisplayDTO> list = reservationDAO.getReservationDisplayList(userId);
//     System.out.println("[DEBUG] Fetched reservation rows count: " + list.size());

//         // 1. Fetch & Bind Active Reservations KPI
//         int activeCount = reservationDAO.getActiveReservationsCount(userId);
//         lblActiveReservations.setText(activeCount + (activeCount == 1 ? " Match" : " Matches"));

//         // 2. Fetch & Bind Next Match Countdown Trend
//         LocalDateTime nextMatch = reservationDAO.getNextMatchTime(userId);
//         if (nextMatch != null) {
//             long hours = Duration.between(LocalDateTime.now(), nextMatch).toHours();
//             lblNextMatch.setText("↑ Next match in " + Math.max(1, hours) + " hours");
//         } else {
//             lblNextMatch.setText("No upcoming matches scheduled");
//         }

//         // 3. Fetch & Bind Pending Approvals KPI
//         int pendingCount = reservationDAO.getPendingApprovalsCount(userId);
//         lblPendingApprovals.setText(pendingCount + (pendingCount == 1 ? " Request" : " Requests"));

//         // 4. Bind Registered Players
//         lblRegisteredPlayers.setText("18 Squad Members");

//         // 5. Populate TableView from DB
//         ObservableList<ReservationDisplayDTO> data = FXCollections.observableArrayList(
//             reservationDAO.getReservationDisplayList(userId)
//         );
//         tblReservations.setItems(data);
//     }

//     @FXML
//     private void handleNewReservation() {
//         System.out.println("[Dashboard] Opening reservation modal...");
//     }

//     @FXML
//     private void handleLogout() {
//         UserSession.logout();
//         SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
//     }
// }



// package com.fieldsync.controllers;

// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.Label;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.TableView;

// public class UserRepDashboardController {

//     // KPI Labels
//     @FXML
//     private Label lblActiveReservations;

//     @FXML
//     private Label lblNextMatch;

//     @FXML
//     private Label lblPendingApprovals;

//     @FXML
//     private Label lblRegisteredPlayers;

//     // TableView and Columns
//     @FXML
//     private TableView<?> tblReservations;

//     @FXML
//     private TableColumn<?, String> colFieldName;

//     @FXML
//     private TableColumn<?, String> colDate;

//     @FXML
//     private TableColumn<?, String> colTime;

//     @FXML
//     private TableColumn<?, String> colStatus;

//     @FXML
//     public void initialize() {
//         // Initialization logic for loading data into labels and tables
//     }

//     // Navigation and Action Event Handlers
//     @FXML
//     private void handleFieldsTab(ActionEvent event) {
//         System.out.println("Navigating to Fields tab...");
//     }

//     @FXML
//     private void handleCalendarTab(ActionEvent event) {
//         System.out.println("Navigating to Calendar tab...");
//     }

//     @FXML
//     private void handleLogout(ActionEvent event) {
//         System.out.println("Logging out...");
//     }

//     @FXML
//     private void handleNewReservation(ActionEvent event) {
//         System.out.println("Opening New Reservation dialog...");
//     }
// }


package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.ReservationDAO.ReservationDisplayDTO;
import com.fieldsync.model.User;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class UserRepDashboardController {

    @FXML private Label lblActiveReservations;
    @FXML private Label lblNextMatch;
    @FXML private Label lblPendingApprovals;
    @FXML private Label lblRegisteredPlayers;

    @FXML private TableView<ReservationDisplayDTO> tblReservations;
    @FXML private TableColumn<ReservationDisplayDTO, String> colFieldName;
    @FXML private TableColumn<ReservationDisplayDTO, String> colDate;
    @FXML private TableColumn<ReservationDisplayDTO, String> colTime;
    @FXML private TableColumn<ReservationDisplayDTO, String> colStatus;

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardData();
    }

    private void setupTableColumns() {
        colFieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblReservations.setPlaceholder(new Label("No upcoming field reservations found."));
    }

    public void loadDashboardData() {
        User currentUser = UserSession.getLoggedInUser();
        System.out.println("[DEBUG] Logged in User: " + (currentUser != null ? currentUser.getEmail() + " (ID: " + currentUser.getId() + ")" : "NULL"));
        
        if (currentUser == null) {
            System.err.println("[Dashboard] No user currently in session!");
            return;
        }

        int userId = currentUser.getId();

        // 1. Fetch & Bind Active Reservations KPI
        int activeCount = reservationDAO.getActiveReservationsCount(userId);
        lblActiveReservations.setText(activeCount + (activeCount == 1 ? " Match" : " Matches"));

        // 2. Fetch & Bind Next Match Countdown Trend
        LocalDateTime nextMatch = reservationDAO.getNextMatchTime(userId);
        if (nextMatch != null) {
            long hours = Duration.between(LocalDateTime.now(), nextMatch).toHours();
            lblNextMatch.setText("↑ Next match in " + Math.max(1, hours) + " hours");
        } else {
            lblNextMatch.setText("No upcoming matches scheduled");
        }

        // 3. Fetch & Bind Pending Approvals KPI
        int pendingCount = reservationDAO.getPendingApprovalsCount(userId);
        lblPendingApprovals.setText(pendingCount + (pendingCount == 1 ? " Request" : " Requests"));

        // 4. Bind Registered Players
        lblRegisteredPlayers.setText("18 Squad Members");

        // 5. Populate TableView from DB
        List<ReservationDisplayDTO> reservationList = reservationDAO.getReservationDisplayList(userId);
        System.out.println("[DEBUG] Fetched reservation rows count: " + reservationList.size());
        
        ObservableList<ReservationDisplayDTO> data = FXCollections.observableArrayList(reservationList);
        tblReservations.setItems(data);
    }

    @FXML
    private void handleNewReservation(ActionEvent event) {
        System.out.println("[Dashboard] Opening reservation modal...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewReservationModal.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("New Reservation Request");
            modalStage.initModality(Modality.APPLICATION_MODAL);

            // Center modal relative to current window
            if (event != null && event.getSource() instanceof javafx.scene.Node) {
                Stage ownerStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                modalStage.initOwner(ownerStage);
            }

            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // Refresh table and KPIs after closing modal
            loadDashboardData();

        } catch (IOException e) {
            System.err.println("[Dashboard] Error loading modal FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFieldsTab(ActionEvent event) {
        System.out.println("Navigating to Fields tab...");
    }

    @FXML
    private void handleCalendarTab(ActionEvent event) {
        System.out.println("Navigating to Calendar tab...");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.logout();
        SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
    }
}