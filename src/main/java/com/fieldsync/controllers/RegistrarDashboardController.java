package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.ReservationDAO.PendingApprovalDTO;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;

public class RegistrarDashboardController {

    @FXML
    private TableView<PendingApprovalDTO> tblApprovals;
    @FXML
    private TableColumn<PendingApprovalDTO, String> colApplicantEmail;
    @FXML
    private TableColumn<PendingApprovalDTO, String> colFullName;
    @FXML
    private TableColumn<PendingApprovalDTO, String> colDepartment;
    @FXML
    private TableColumn<PendingApprovalDTO, String> colDateTime;
    @FXML
    private TableColumn<PendingApprovalDTO, String> colPurpose;
    @FXML
    private TableColumn<PendingApprovalDTO, Void> colActions;

    @FXML
    private BarChart<String, Number> chartUsage;
    @FXML
    private PieChart chartDepartment;

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadPendingApprovals();
    }

    private void setupTableColumns() {
        colApplicantEmail.setCellValueFactory(new PropertyValueFactory<>("applicantEmail"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTimeSlot"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Approve");
            private final Button btnReject = new Button("Reject");
            private final HBox actionBox = new HBox(8, btnApprove, btnReject);

            {
                btnApprove.setStyle(
                        "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnReject.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                actionBox.setAlignment(Pos.CENTER);

                btnApprove.setOnAction(event -> {
                    PendingApprovalDTO dto = getTableView().getItems().get(getIndex());
                    handleDecision(dto, "CONFIRMED");
                });

                btnReject.setOnAction(event -> {
                    PendingApprovalDTO dto = getTableView().getItems().get(getIndex());
                    handleDecision(dto, "CANCELLED");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        tblApprovals.setPlaceholder(new Label("No pending reservation requests to review."));
    }

    private void loadPendingApprovals() {
        List<PendingApprovalDTO> pendingList = reservationDAO.getAllPendingReservations();
        if (pendingList != null) {
            ObservableList<PendingApprovalDTO> data = FXCollections.observableArrayList(pendingList);
            tblApprovals.setItems(data);
            tblApprovals.refresh();
        }
    }

    private void handleDecision(PendingApprovalDTO dto, String status) {
        boolean success = reservationDAO.updateReservationStatus(
                dto.getReservationId(),
                dto.getApplicantId(),
                status,
                dto.getFieldName());

        if (success) {
            System.out.println("[Registrar] Reservation " + dto.getReservationId() + " marked as " + status);
            loadPendingApprovals();
        } else {
            System.err.println("[Registrar] Failed to update reservation " + dto.getReservationId());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.logout();
        SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
    }
}