package com.fieldsync.components;

import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.ReservationDAO.ReservationDisplayDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class UpNextMatchesView extends VBox {

    private final TableView<ReservationDisplayDTO> table = new TableView<>();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public UpNextMatchesView() {
        setSpacing(12);
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #0f172a; -fx-background-radius: 12;");

        Label title = new Label("Upcoming Confirmed Matches");
        title.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 18px; -fx-font-weight: bold;");

        setupTable();
        getChildren().addAll(title, table);
    }

    private void setupTable() {
        TableColumn<ReservationDisplayDTO, String> colField = new TableColumn<>("Field");
        colField.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
        colField.setPrefWidth(180);

        TableColumn<ReservationDisplayDTO, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(120);

        TableColumn<ReservationDisplayDTO, String> colTime = new TableColumn<>("Time Slot");
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colTime.setPrefWidth(180);

        TableColumn<ReservationDisplayDTO, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);

        table.getColumns().addAll(colField, colDate, colTime, colStatus);
        table.setPlaceholder(new Label("No confirmed upcoming matches found."));
        table.setStyle("-fx-background-color: #1e293b; -fx-control-inner-background: #1e293b;");
    }

    public void loadMatches(int userId) {
        List<ReservationDisplayDTO> confirmedMatches = reservationDAO.getUpcomingConfirmedMatches(userId);
        table.setItems(FXCollections.observableArrayList(confirmedMatches));
    }
}