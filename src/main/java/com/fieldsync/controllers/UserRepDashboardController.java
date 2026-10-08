package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.ReservationDAO.ReservationDisplayDTO;
import com.fieldsync.model.User;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;
import com.fieldsync.views.ManageRosterDialog;
import com.fieldsync.views.NewReservationDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class UserRepDashboardController {

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
                            loadDashboardData();
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
                        btnRoster.setStyle(
                                "-fx-background-color: #334155; -fx-text-fill: #64748b; -fx-font-size: 11px;");
                    } else {
                        btnRoster.setStyle(
                                "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
                    }
                    setGraphic(btnRoster);
                }
            }
        });

        tblReservations.setPlaceholder(new Label("No upcoming field reservations found."));
    }

    public void loadDashboardData() {
        User currentUser = UserSession.getLoggedInUser();
        if (currentUser == null)
            return;

        lblWelcome.setText("WELCOME, " + currentUser.getName().toUpperCase());

        int userId = currentUser.getId();

        int activeCount = reservationDAO.getActiveReservationsCount(userId);
        lblActiveReservations.setText(activeCount + (activeCount == 1 ? " Match" : " Matches"));

        LocalDateTime nextMatch = reservationDAO.getNextMatchTime(userId);
        if (nextMatch != null) {
            long hours = Duration.between(LocalDateTime.now(), nextMatch).toHours();
            lblNextMatch.setText("↑ Next match in " + Math.max(1, hours) + " hours");
        } else {
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
            loadDashboardData();
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
