package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.ReservationDAO.PendingApprovalDTO;
import com.fieldsync.dao.ReservationDAO.WeeklyUsageDTO;
import com.fieldsync.dao.ReservationDAO.DepartmentAllocationDTO;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegistrarDashboardController {

    @FXML
    private Label lblDateRange;
    @FXML
    private Label lblWeeklyUsageTitle;

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

    // Department Allocation Table components
    @FXML
    private TableView<DepartmentAllocationDTO> tblDepartmentAllocation;
    @FXML
    private TableColumn<DepartmentAllocationDTO, String> colDeptName;
    @FXML
    private TableColumn<DepartmentAllocationDTO, Integer> colDeptConfirmedCount;

    @FXML
    private BarChart<String, Number> chartUsage;

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardAnalytics();
        loadPendingApprovals();
    }

    private void setupTableColumns() {
        // Pending Approvals Table
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

        // Department Allocation Table Columns & Conditional Red Row Highlighting (>5 Confirmed)
        colDeptName.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDeptConfirmedCount.setCellValueFactory(new PropertyValueFactory<>("confirmedCount"));

        tblDepartmentAllocation.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DepartmentAllocationDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    // Highlight row red if confirmed count > 5 in the past 7 days
                    if (item.getConfirmedCount() > 5) {
                        setStyle("-fx-background-color: #7f1d1d !important; -fx-text-background-color: white;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tblDepartmentAllocation.setPlaceholder(new Label("No department usage recorded in the past 7 days."));
    }

    private void loadDashboardAnalytics() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        String dateRangeStr = weekAgo.format(displayFormatter) + " to " + today.format(displayFormatter);
        if (lblDateRange != null) {
            lblDateRange.setText("(" + dateRangeStr + ")");
        }
        if (lblWeeklyUsageTitle != null) {
            lblWeeklyUsageTitle.setText("WEEKLY FIELD USAGE (" + dateRangeStr + ")");
        }

        // 1. Load BarChart Usage
        chartUsage.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Hours Reserved");

        List<WeeklyUsageDTO> usageList = reservationDAO.getWeeklyUsageStats();
        for (WeeklyUsageDTO dto : usageList) {
            series.getData().add(new XYChart.Data<>(dto.getDayLabel(), dto.getTotalHours()));
        }
        chartUsage.getData().add(series);

        // 2. Load Department Allocations Table
        List<DepartmentAllocationDTO> deptList = reservationDAO.getDepartmentAllocations();
        ObservableList<DepartmentAllocationDTO> deptData = FXCollections.observableArrayList(deptList);
        tblDepartmentAllocation.setItems(deptData);
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
            loadDashboardAnalytics(); // Refresh analytics when decisions change
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