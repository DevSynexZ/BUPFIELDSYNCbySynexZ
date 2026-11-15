// package com.fieldsync.controllers;

// import com.fieldsync.config.AppConfig;
// import com.fieldsync.dao.ReservationDAO;
// import com.fieldsync.dao.ReservationDAO.ReservationDisplayDTO;
// import com.fieldsync.model.User;
// import com.fieldsync.navigation.SceneManager;
// import com.fieldsync.util.UserSession;
// import com.fieldsync.views.ManageRosterDialog;
// import com.fieldsync.views.NewReservationDialog;

// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// public class UserRepDashboardController {

//     @FXML
//     private Label lblWelcome;

//     @FXML
//     private Label lblActiveReservations;
//     @FXML
//     private Label lblNextMatch;
//     @FXML
//     private Label lblPendingApprovals;

//     @FXML
//     private TableView<ReservationDisplayDTO> tblReservations;
//     @FXML
//     private TableColumn<ReservationDisplayDTO, String> colFieldName;
//     @FXML
//     private TableColumn<ReservationDisplayDTO, String> colDate;
//     @FXML
//     private TableColumn<ReservationDisplayDTO, String> colTime;
//     @FXML
//     private TableColumn<ReservationDisplayDTO, String> colPurpose;
//     @FXML
//     private TableColumn<ReservationDisplayDTO, String> colStatus;
//     @FXML
//     private TableColumn<ReservationDisplayDTO, Void> colRoster;
//     @FXML private TableColumn<ReservationDisplayDTO,Void> colDelete;

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
//         colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
//         colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

//         colRoster.setCellFactory(param -> new TableCell<>() {
//             private final Button btnRoster = new Button("Roster");

//             {
//                 btnRoster.setOnAction(e -> {
//                     ReservationDisplayDTO item = getTableView().getItems().get(getIndex());
//                     if (item != null) {
//                         boolean updated = ManageRosterDialog.display(item.getReservationId(), item.getPurpose());
//                         if (updated) {
//                             loadDashboardData();
//                         }
//                     }
//                 });
//             }

//             @Override
//             protected void updateItem(Void item, boolean empty) {
//                 super.updateItem(item, empty);
//                 if (empty) {
//                     setGraphic(null);
//                 } else {
//                     ReservationDisplayDTO reservation = getTableView().getItems().get(getIndex());
//                     boolean isConfirmed = "CONFIRMED".equalsIgnoreCase(reservation.getStatus());
//                     btnRoster.setDisable(!isConfirmed);

//                     if (!isConfirmed) {
//                         btnRoster.setStyle(
//                                 "-fx-background-color: #334155; -fx-text-fill: #64748b; -fx-font-size: 11px;");
//                     } else {
//                         btnRoster.setStyle(
//                                 "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
//                     }
//                     setGraphic(btnRoster);
//                 }
//             }
//         });

//         // Delete / Cancel Button Column Setup
//         colDelete.setCellFactory(param -> new TableCell<>() {
//             private final Button btnDelete = new Button("Cancel");

//             {
//                 btnDelete.setOnAction(e -> {
//                     ReservationDisplayDTO item = getTableView().getItems().get(getIndex());
//                     if (item != null) {
//                         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                         alert.setTitle("Cancel Reservation");
//                         alert.setHeaderText("Delete/Cancel Reservation");
//                         alert.setContentText("Are you sure you want to cancel the reservation for " + item.getPurpose() + "?");

//                         Optional<ButtonType> result = alert.showAndWait();
//                         if (result.isPresent() && result.get() == ButtonType.OK) {
//                             boolean success = reservationDAO.deleteReservation(item.getReservationId());
//                             if (success) {
//                                 loadDashboardData();
//                             } else {
//                                 Alert errAlert = new Alert(Alert.AlertType.ERROR);
//                                 errAlert.setTitle("Error");
//                                 errAlert.setHeaderText("Could not cancel reservation");
//                                 errAlert.setContentText("Database operation failed.");
//                                 errAlert.showAndWait();
//                             }
//                         }
//                     }
//                 });
//             }

//             @Override
//             protected void updateItem(Void item, boolean empty) {
//                 super.updateItem(item, empty);
//                 if (empty) {
//                     setGraphic(null);
//                 } else {
//                     btnDelete.setStyle(
//                             "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
//                     setGraphic(btnDelete);
//                 }
//             }
//         });

//         tblReservations.setPlaceholder(new Label("No upcoming field reservations found."));
//     }

//     public void loadDashboardData() {
//         User currentUser = UserSession.getLoggedInUser();
//         if (currentUser == null)
//             return;

//         lblWelcome.setText("WELCOME, " + currentUser.getName().toUpperCase());

//         int userId = currentUser.getId();

//         int activeCount = reservationDAO.getActiveReservationsCount(userId);
//         LocalDateTime nextMatch = reservationDAO.getNextMatchTime(userId); // Fixed method call here

//         if (activeCount > 0 && nextMatch != null) {
//             lblActiveReservations.setText(activeCount + (activeCount == 1 ? " Match" : " Matches"));

//             long hours = Duration.between(LocalDateTime.now(), nextMatch).toHours();
//             lblNextMatch.setText("↑ Next match in " + Math.max(1, hours) + " hours");
//         } else {
//             lblActiveReservations.setText("0 Matches");
//             lblNextMatch.setText("No upcoming matches scheduled");
//         }

//         int pendingCount = reservationDAO.getPendingApprovalsCount(userId);
//         lblPendingApprovals.setText(pendingCount + (pendingCount == 1 ? " Request" : " Requests"));

//         List<ReservationDisplayDTO> reservationList = reservationDAO.getReservationDisplayList(userId);
//         ObservableList<ReservationDisplayDTO> data = FXCollections.observableArrayList(reservationList);
//         tblReservations.setItems(data);
//     }

//     @FXML
//     private void handleNewReservation(ActionEvent event) {
//         User currentUser = UserSession.getLoggedInUser();
//         if (currentUser == null)
//             return;

//         boolean isCreated = NewReservationDialog.display(currentUser);
//         if (isCreated) {
//             loadDashboardData();
//         }
//     }

//     @FXML
//     private void handleFieldsTab(ActionEvent event) {
//         System.out.println("Navigating to Fields tab...");
//     }

//     @FXML
//     private void handleCalendarTab(ActionEvent event) {
//         // Switch center workspace to Upcoming Matches view
//         SceneManager.switchScene(AppConfig.VIEW_UPCOMING_MATCHES, AppConfig.STYLE_DASHBOARD);
//     }

//     @FXML
//     private void handleLogout(ActionEvent event) {
//         UserSession.logout();
//         SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
//     }
// }
















package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.ReservationDAO.ReservationDisplayDTO;
import com.fieldsync.model.User;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.LoadingOverlay;
import com.fieldsync.util.UserSession;
import com.fieldsync.views.ManageRosterDialog;
import com.fieldsync.views.NewReservationDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserRepDashboardController {

    @FXML
    private StackPane rootPane;
    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblActiveReservations;
    @FXML
    private Label lblNextMatch;
    @FXML
    private Label lblPendingApprovals;

    @FXML
    private TableView<ReservationDisplayDTO> tblReservations;
    @FXML
    private TableColumn<ReservationDisplayDTO, String> colFieldName;
    @FXML
    private TableColumn<ReservationDisplayDTO, String> colDate;
    @FXML
    private TableColumn<ReservationDisplayDTO, String> colTime;
    @FXML
    private TableColumn<ReservationDisplayDTO, String> colPurpose;
    @FXML
    private TableColumn<ReservationDisplayDTO, String> colStatus;
    @FXML
    private TableColumn<ReservationDisplayDTO, Void> colRoster;
    @FXML
    private TableColumn<ReservationDisplayDTO, Void> colDelete;

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardDataAsync();
    }

    private void setupTableColumns() {
        colFieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colRoster.setCellFactory(param -> new TableCell<>() {
            private final Button btnRoster = new Button("Roster");

            {
                btnRoster.setOnAction(e -> {
                    ReservationDisplayDTO item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        boolean updated = ManageRosterDialog.display(item.getReservationId(), item.getPurpose());
                        if (updated) {
                            loadDashboardDataAsync();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ReservationDisplayDTO reservation = getTableView().getItems().get(getIndex());
                    boolean isConfirmed = "CONFIRMED".equalsIgnoreCase(reservation.getStatus());
                    btnRoster.setDisable(!isConfirmed);

                    if (!isConfirmed) {
                        btnRoster.setStyle("-fx-background-color: #334155; -fx-text-fill: #64748b; -fx-font-size: 11px;");
                    } else {
                        btnRoster.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
                    }
                    setGraphic(btnRoster);
                }
            }
        });

        colDelete.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Cancel");

            {
                btnDelete.setOnAction(e -> {
                    ReservationDisplayDTO item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Cancel Reservation");
                        alert.setHeaderText("Delete/Cancel Reservation");
                        alert.setContentText("Are you sure you want to cancel the reservation for " + item.getPurpose() + "?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            boolean success = reservationDAO.deleteReservation(item.getReservationId());
                            if (success) {
                                loadDashboardDataAsync();
                            } else {
                                Alert errAlert = new Alert(Alert.AlertType.ERROR);
                                errAlert.setTitle("Error");
                                errAlert.setHeaderText("Could not cancel reservation");
                                errAlert.setContentText("Database operation failed.");
                                errAlert.showAndWait();
                            }
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
                    setGraphic(btnDelete);
                }
            }
        });

        tblReservations.setPlaceholder(new Label("No upcoming field reservations found."));
    }

    public void loadDashboardDataAsync() {
        if (rootPane == null) {
            loadDashboardData();
            return;
        }

        StackPane loadingOverlay = LoadingOverlay.createOverlay(rootPane);
        rootPane.getChildren().add(loadingOverlay);

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Background execution to fetch remote Render DB data
                return null;
            }
        };

        loadTask.setOnSucceeded(e -> {
            rootPane.getChildren().remove(loadingOverlay);
            loadDashboardData();
        });

        loadTask.setOnFailed(e -> {
            rootPane.getChildren().remove(loadingOverlay);
            System.err.println("[UserRepDashboard] Failed to load user rep data.");
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
    }

    public void loadDashboardData() {
        User currentUser = UserSession.getLoggedInUser();
        if (currentUser == null)
            return;

        lblWelcome.setText("WELCOME, " + currentUser.getName().toUpperCase());

        int userId = currentUser.getId();

        int activeCount = reservationDAO.getActiveReservationsCount(userId);
        LocalDateTime nextMatch = reservationDAO.getNextMatchTime(userId);

        if (activeCount > 0 && nextMatch != null) {
            lblActiveReservations.setText(activeCount + (activeCount == 1 ? " Match" : " Matches"));

            long hours = Duration.between(LocalDateTime.now(), nextMatch).toHours();
            lblNextMatch.setText("↑ Next match in " + Math.max(1, hours) + " hours");
        } else {
            lblActiveReservations.setText("0 Matches");
            lblNextMatch.setText("No upcoming matches scheduled");
        }

        int pendingCount = reservationDAO.getPendingApprovalsCount(userId);
        lblPendingApprovals.setText(pendingCount + (pendingCount == 1 ? " Request" : " Requests"));

        List<ReservationDisplayDTO> reservationList = reservationDAO.getReservationDisplayList(userId);
        ObservableList<ReservationDisplayDTO> data = FXCollections.observableArrayList(reservationList);
        tblReservations.setItems(data);
    }

    @FXML
    private void handleNewReservation(ActionEvent event) {
        User currentUser = UserSession.getLoggedInUser();
        if (currentUser == null)
            return;

        boolean isCreated = NewReservationDialog.display(currentUser);
        if (isCreated) {
            loadDashboardDataAsync();
        }
    }

    @FXML
    private void handleFieldsTab(ActionEvent event) {
        System.out.println("Navigating to Fields tab...");
    }

    @FXML
    private void handleCalendarTab(ActionEvent event) {
        SceneManager.switchScene(AppConfig.VIEW_UPCOMING_MATCHES, AppConfig.STYLE_DASHBOARD);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.logout();
        SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
    }
}