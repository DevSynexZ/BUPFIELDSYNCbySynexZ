package com.fieldsync.views;

import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.model.MatchRoster;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ManageRosterDialog {

    public static boolean display(int reservationId, String matchTitle) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Update Squad Roster - " + (matchTitle != null ? matchTitle : "Match"));
        window.setMinWidth(520);

        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0f172a;");

        Label lblTitle = new Label("Match Squad Roster");
        lblTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Leadership Grid
        GridPane leadershipGrid = new GridPane();
        leadershipGrid.setHgap(12);
        leadershipGrid.setVgap(10);

        Label lblCaptain = new Label("Team Captain:");
        lblCaptain.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;");
        TextField txtCaptain = new TextField();
        txtCaptain.setPromptText("Captain's Full Name");

        Label lblManager = new Label("Team Manager:");
        lblManager.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;");
        TextField txtManager = new TextField();
        txtManager.setPromptText("Manager / Coach Name");

        leadershipGrid.add(lblCaptain, 0, 0);
        leadershipGrid.add(txtCaptain, 1, 0);
        leadershipGrid.add(lblManager, 0, 1);
        leadershipGrid.add(txtManager, 1, 1);

        // Player Input Slots
        Label lblPlayers = new Label("Squad Players (Max 18):");
        lblPlayers.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane playerGrid = new GridPane();
        playerGrid.setHgap(10);
        playerGrid.setVgap(8);

        List<TextField> playerInputs = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            TextField txtPlayer = new TextField();
            txtPlayer.setPromptText("Player " + (i + 1));
            txtPlayer.setPrefWidth(220);
            playerInputs.add(txtPlayer);

            int col = i % 2;
            int row = i / 2;
            playerGrid.add(txtPlayer, col, row);
        }

        // Prefill Existing Data if available
        ReservationDAO dao = new ReservationDAO();
        MatchRoster existingRoster = dao.getRosterByReservationId(reservationId);
        if (existingRoster != null) {
            txtCaptain.setText(existingRoster.getCaptainName());
            txtManager.setText(existingRoster.getManagerName());
            List<String> existingPlayers = existingRoster.getPlayers();
            for (int i = 0; i < Math.min(existingPlayers.size(), 18); i++) {
                playerInputs.get(i).setText(existingPlayers.get(i));
            }
        }

        ScrollPane scrollPane = new ScrollPane(playerGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(260);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Label lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px;");

        Button btnSave = new Button("Save Roster");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");

        final boolean[] result = {false};

        btnSave.setOnAction(e -> {
            List<String> players = new ArrayList<>();
            for (TextField tf : playerInputs) {
                if (!tf.getText().trim().isEmpty()) {
                    players.add(tf.getText().trim());
                }
            }

            if (dao.saveRoster(reservationId, txtCaptain.getText().trim(), txtManager.getText().trim(), players)) {
                result[0] = true;
                window.close();
            } else {
                lblStatus.setText("Failed to save match roster.");
            }
        });

        root.getChildren().addAll(lblTitle, leadershipGrid, lblPlayers, scrollPane, lblStatus, btnSave);

        Scene scene = new Scene(root, 540, 580);
        window.setScene(scene);
        window.showAndWait();

        return result[0];
    }
}