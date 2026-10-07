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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class NewReservationDialog {

    public static boolean display(User currentUser) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("New Field Reservation Request");
        window.setMinWidth(520);

        VBox layout = new VBox(18);
        layout.setPadding(new Insets(32));
        layout.setStyle("-fx-background-color: #0f172a;");

        Label lblTitle = new Label("Create Reservation Request");
        lblTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 22px; -fx-font-weight: bold;");

        // Field Selector
        Label lblField = new Label("Select Field:");
        lblField.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<Field> cmbFields = new ComboBox<>();
        cmbFields.setMaxWidth(Double.MAX_VALUE);
        cmbFields.setStyle("-fx-font-size: 14px; -fx-background-color: #1e293b; -fx-mark-color: white;");

        // String converter to format display text
        cmbFields.setConverter(new StringConverter<Field>() {
            @Override
            public String toString(Field field) {
                return field != null ? field.getFieldName() : "";
            }

            @Override
            public Field fromString(String string) {
                return null;
            }
        });

        // Cell factory for dropdown menu list items
        cmbFields.setCellFactory(lv -> new ListCell<Field>() {
            @Override
            protected void updateItem(Field item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFieldName());
                setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");
            }
        });

        // Button cell factory for the currently selected item display
        cmbFields.setButtonCell(new ListCell<Field>() {
            @Override
            protected void updateItem(Field item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFieldName());
                setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");
            }
        });

        FieldDAO fieldDAO = new FieldDAO();
        List<Field> activeFields = fieldDAO.getAllActiveFields();

        if (activeFields != null && !activeFields.isEmpty()) {
            cmbFields.getItems().addAll(activeFields);
            cmbFields.getSelectionModel().selectFirst();
        }

        // Date Picker
        Label lblDate = new Label("Reservation Date:");
        lblDate.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-font-size: 14px;");

        // Time Selectors
        Label lblTime = new Label("Time Slot (Start - End Hour):");
        lblTime.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox timeBox = new HBox(12);
        Label lblFrom = new Label("From:");
        lblFrom.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");
        Label lblTo = new Label("To:");
        lblTo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");

        ComboBox<Integer> cmbStartHour = new ComboBox<>();
        ComboBox<Integer> cmbEndHour = new ComboBox<>();
        cmbStartHour.setStyle("-fx-font-size: 14px;");
        cmbEndHour.setStyle("-fx-font-size: 14px;");

        for (int h = 6; h <= 22; h++) {
            cmbStartHour.getItems().add(h);
            cmbEndHour.getItems().add(h);
        }
        cmbStartHour.getSelectionModel().select(Integer.valueOf(16));
        cmbEndHour.getSelectionModel().select(Integer.valueOf(18));

        timeBox.getChildren().addAll(lblFrom, cmbStartHour, lblTo, cmbEndHour);

        // Purpose Input
        Label lblPurpose = new Label("Purpose / Match Details:");
        lblPurpose.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        TextField txtPurpose = new TextField();
        txtPurpose.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");
        txtPurpose.setPromptText("e.g. Inter-Department Friendly Match");

        // Status Output
        Label lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Action Button
        Button btnSubmit = new Button("Submit Request");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setStyle(
                "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 24; -fx-cursor: hand;");

        final boolean[] success = { false };

        btnSubmit.setOnAction(e -> {
            Field selectedField = cmbFields.getValue();
            LocalDate date = datePicker.getValue();
            Integer startHour = cmbStartHour.getValue();
            Integer endHour = cmbEndHour.getValue();
            String purpose = txtPurpose.getText().trim();

            if (selectedField == null || date == null || startHour == null || endHour == null || purpose.isEmpty()) {
                lblStatus.setText("Please select a field and fill out all fields.");
                return;
            }

            if (startHour >= endHour) {
                lblStatus.setText("End time must be after start time.");
                return;
            }

            LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(startHour, 0));
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.of(endHour, 0));

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
                lblTitle,
                lblField, cmbFields,
                lblDate, datePicker,
                lblTime, timeBox,
                lblPurpose, txtPurpose,
                lblStatus, btnSubmit);

        Scene scene = new Scene(layout, 520, 680);
        window.setScene(scene);
        window.showAndWait();

        return success[0];
    }
}