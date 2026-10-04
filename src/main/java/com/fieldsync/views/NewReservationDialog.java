package com.fieldsync.views;

import com.fieldsync.dao.FieldDAO;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.model.Field;
import com.fieldsync.model.Reservation;
import com.fieldsync.model.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class NewReservationDialog {

    public static boolean display(User currentUser) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("New Field Reservation Request");
        window.setMinWidth(420);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: #0f172a;");

        Label lblTitle = new Label("Create Reservation Request");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Field Dropdown (Using your existing FieldDAO method)
        Label lblField = new Label("Select Field:");
        lblField.setStyle("-fx-text-fill: #94a3b8;");
        ComboBox<Field> cmbFields = new ComboBox<>();
        cmbFields.setMaxWidth(Double.MAX_VALUE);
        FieldDAO fieldDAO = new FieldDAO();
        List<Field> activeFields = fieldDAO.getAllActiveFields();
        cmbFields.getItems().addAll(activeFields);
        if (!activeFields.isEmpty())
            cmbFields.getSelectionModel().selectFirst();

        // Date Picker
        Label lblDate = new Label("Reservation Date:");
        lblDate.setStyle("-fx-text-fill: #94a3b8;");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);

        // Time Selectors
        Label lblTime = new Label("Time Slot (Start - End Hour):");
        lblTime.setStyle("-fx-text-fill: #94a3b8;");
        HBox timeBox = new HBox(10);
        ComboBox<Integer> cmbStartHour = new ComboBox<>();
        ComboBox<Integer> cmbEndHour = new ComboBox<>();
        for (int h = 6; h <= 22; h++) {
            cmbStartHour.getItems().add(h);
            cmbEndHour.getItems().add(h);
        }
        cmbStartHour.getSelectionModel().select(Integer.valueOf(16)); // 4 PM default
        cmbEndHour.getSelectionModel().select(Integer.valueOf(18)); // 6 PM default
        timeBox.getChildren().addAll(new Label("From:"), cmbStartHour, new Label("To:"), cmbEndHour);

        // Purpose Input
        Label lblPurpose = new Label("Purpose / Match Details:");
        lblPurpose.setStyle("-fx-text-fill: #94a3b8;");
        TextField txtPurpose = new TextField();
        txtPurpose.setPromptText("e.g. Inter-Department Friendly Match");

        // Status Feedback
        Label lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: #ef4444;");

        // Buttons
        Button btnSubmit = new Button("Submit Request");
        btnSubmit.setStyle(
                "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        final boolean[] success = { false };

        btnSubmit.setOnAction(e -> {
            Field selectedField = cmbFields.getValue();
            LocalDate date = datePicker.getValue();
            Integer startHour = cmbStartHour.getValue();
            Integer endHour = cmbEndHour.getValue();
            String purpose = txtPurpose.getText().trim();

            if (selectedField == null || date == null || startHour == null || endHour == null || purpose.isEmpty()) {
                lblStatus.setText("Please fill out all fields.");
                return;
            }

            if (startHour >= endHour) {
                lblStatus.setText("End time must be after start time.");
                return;
            }

            LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(startHour, 0));
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.of(endHour, 0));

            // Reservation newRes = new Reservation(
            // 0, selectedField.getFieldId(), currentUser.getId(), null,
            // startDateTime, endDateTime, "PENDING_APPROVAL", purpose, LocalDateTime.now()
            // );

            Reservation newRes = new Reservation(
                    0, selectedField.getFieldId(), currentUser.getId(), null,
                    startDateTime, endDateTime, "PENDING_APPROVAL", purpose, LocalDateTime.now());

            ReservationDAO dao = new ReservationDAO();
            if (dao.createReservation(newRes)) {
                success[0] = true;
                window.close();
            } else {
                lblStatus.setText("Time slot unavailable or overlap detected!");
            }
        });

        layout.getChildren().addAll(
                lblTitle, lblField, cmbFields, lblDate, datePicker,
                lblTime, timeBox, lblPurpose, txtPurpose, lblStatus, btnSubmit);

        Scene scene = new Scene(layout, 420, 480);
        window.setScene(scene);
        window.showAndWait();

        return success[0];
    }
}