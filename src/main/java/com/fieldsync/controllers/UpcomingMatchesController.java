
// package com.fieldsync.controllers;

// import com.fieldsync.config.AppConfig;
// import com.fieldsync.dao.ReservationDAO;
// import com.fieldsync.navigation.SceneManager;
// import com.fieldsync.util.UserSession;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

// public class UpcomingMatchesController {

//     public static class CampusMatchDTO {
//         private final String fieldName;
//         private final String date;
//         private final String timeSlot;
//         private final String purpose;
//         private final String countdown;

//         public CampusMatchDTO(String fieldName, String date, String timeSlot, String purpose, LocalDateTime startTime) {
//             this.fieldName = fieldName;
//             this.date = date;
//             this.timeSlot = timeSlot;
//             this.purpose = purpose;

//             long hours = Duration.between(LocalDateTime.now(), startTime).toHours();
//             if (hours <= 0) {
//                 this.countdown = "Happening Now / Started";
//             } else {
//                 this.countdown = "In " + hours + " hours";
//             }
//         }

//         public String getFieldName() { return fieldName; }
//         public String getDate() { return date; }
//         public String getTimeSlot() { return timeSlot; }
//         public String getPurpose() { return purpose; }
//         public String getCountdown() { return countdown; }
//     }

//     @FXML private TableView<CampusMatchDTO> tblUpcomingMatches;
//     @FXML private TableColumn<CampusMatchDTO, String> colFieldName;
//     @FXML private TableColumn<CampusMatchDTO, String> colDate;
//     @FXML private TableColumn<CampusMatchDTO, String> colTime;
//     @FXML private TableColumn<CampusMatchDTO, String> colPurpose;
//     @FXML private TableColumn<CampusMatchDTO, String> colCountdown;

//     private final ReservationDAO reservationDAO = new ReservationDAO();
//     private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
//     private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

//     @FXML
//     public void initialize() {
//         colFieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
//         colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
//         colTime.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
//         colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
//         colCountdown.setCellValueFactory(new PropertyValueFactory<>("countdown"));

//         loadAllCampusMatches();
//         tblUpcomingMatches.setPlaceholder(new Label("No upcoming confirmed campus matches scheduled."));
//     }

//     private void loadAllCampusMatches() {
//         // Fetch raw database reservations directly with start times
//         String sql = "SELECT f.field_name, r.start_time, r.end_time, r.purpose FROM reservations r " +
//                      "JOIN fields f ON r.field_id = f.field_id " +
//                      "WHERE r.status = 'CONFIRMED'::reservation_status AND r.start_time >= NOW() " +
//                      "ORDER BY r.start_time ASC";

//         ObservableList<CampusMatchDTO> list = FXCollections.observableArrayList();

//         try (var conn = com.fieldsync.config.DatabaseConnection.getConnection();
//              var stmt = conn.prepareStatement(sql);
//              var rs = stmt.executeQuery()) {

//             while (rs.next()) {
//                 LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
//                 LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();

//                 String fieldName = rs.getString("field_name");
//                 String dateStr = start.format(DATE_FMT);
//                 String timeSlotStr = start.format(TIME_FMT) + " - " + end.format(TIME_FMT);
//                 String purpose = rs.getString("purpose");

//                 list.add(new CampusMatchDTO(fieldName, dateStr, timeSlotStr, purpose, start));
//             }
//         } catch (Exception e) {
//             System.err.println("[UpcomingMatchesController] Error loading campus matches: " + e.getMessage());
//         }

//         tblUpcomingMatches.setItems(list);
//     }

//     @FXML
//     private void handleHomeTab(ActionEvent event) {
//         SceneManager.switchScene(AppConfig.VIEW_STUDENT_DASHBOARD, AppConfig.STYLE_DASHBOARD);
//     }

//     @FXML
//     private void handleFieldsTab(ActionEvent event) {
//         System.out.println("Navigating to Fields tab...");
//     }

//     @FXML
//     private void handleLogout(ActionEvent event) {
//         UserSession.logout();
//         SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
//     }
// }

package com.fieldsync.controllers;

import com.fieldsync.config.AppConfig;
import com.fieldsync.navigation.SceneManager;
import com.fieldsync.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.net.URL;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpcomingMatchesController {

    @FXML
    private HBox cardsContainer;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    @FXML
    public void initialize() {
        loadAllCampusMatches();
    }

    private void loadAllCampusMatches() {
        cardsContainer.getChildren().clear();

        String sql = "SELECT f.field_name, r.start_time, r.end_time, r.purpose FROM reservations r " +
                "JOIN fields f ON r.field_id = f.field_id " +
                "WHERE r.status = 'CONFIRMED'::reservation_status AND r.start_time >= NOW() " +
                "ORDER BY r.start_time ASC";

        int cardIndex = 0;

        try (var conn = com.fieldsync.config.DatabaseConnection.getConnection();
                var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();

                String fieldName = rs.getString("field_name");
                String dateStr = start.format(DATE_FMT);
                String timeSlotStr = start.format(TIME_FMT) + " - " + end.format(TIME_FMT);
                String purpose = rs.getString("purpose");

                long hours = Duration.between(LocalDateTime.now(), start).toHours();
                String countdownText = (hours <= 0) ? "⚡ Happening Now" : "⏳ In " + hours + " hours";

                // Cycle through 3 background images
                int bgImageIndex = (cardIndex % 3) + 1;
                URL bgUrl = getClass().getResource("/images/match_bg" + bgImageIndex + ".jpeg");
                String bgStyle = (bgUrl != null)
                        ? "-fx-background-image: url('" + bgUrl.toExternalForm()
                                + "'); -fx-background-size: cover; -fx-background-position: center;"
                        : "-fx-background-color: #1a1a2e;";

                // Build Sleek Card Node
                VBox card = new VBox(12);
                card.setPrefWidth(280);
                card.setPrefHeight(200);
                card.setPadding(new Insets(18));
                card.setAlignment(Pos.BOTTOM_LEFT);
                // card.setStyle(bgStyle + " -fx-background-radius: 12px; -fx-border-radius:
                // 12px; -fx-border-color: rgba(255,255,255,0.1);");

                String baseStyle = bgStyle
                        + " -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-border-color: rgba(255,255,255,0.1); -fx-cursor: hand;";
                String hoverStyle = bgStyle
                        + " -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-border-color: #6366f1; -fx-effect: dropshadow(three-pass-box, rgba(99, 102, 241, 0.4), 15, 0, 0, 4); -fx-cursor: hand;";

                card.setStyle(baseStyle);

                // Add Smooth Hover Effects
                card.setOnMouseEntered(e -> {
                    card.setStyle(hoverStyle);
                    card.setScaleX(1.03);
                    card.setScaleY(1.03);
                });

                card.setOnMouseExited(e -> {
                    card.setStyle(baseStyle);
                    card.setScaleX(1.0);
                    card.setScaleY(1.0);
                });

                // Dark gradient overlay box inside card for supreme readability
                VBox overlay = new VBox(8);
                overlay.setStyle(
                        "-fx-background-color: rgba(13, 21, 36, 0.85); -fx-background-radius: 8px; -fx-padding: 12px;");

                Label lblPurpose = new Label(purpose);
                lblPurpose.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px;");

                Label lblField = new Label("🏟 " + fieldName);
                lblField.setStyle("-fx-text-fill: #a5b4fc; -fx-font-size: 12px;");

                Label lblDateTime = new Label("📅 " + dateStr + " | " + timeSlotStr);
                lblDateTime.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 11px;");

                Label lblCountdown = new Label(countdownText);
                lblCountdown.setStyle(
                        "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-size: 10px;");

                HBox footer = new HBox(lblCountdown);
                footer.setAlignment(Pos.CENTER_LEFT);

                overlay.getChildren().addAll(lblPurpose, lblField, lblDateTime, footer);
                card.getChildren().add(overlay);
                cardsContainer.getChildren().add(card);

                cardIndex++;
            }
        } catch (Exception e) {
            System.err.println("[UpcomingMatchesController] Error loading campus matches: " + e.getMessage());
        }

        if (cardsContainer.getChildren().isEmpty()) {
            Label emptyLbl = new Label("No upcoming confirmed campus matches scheduled.");
            emptyLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
            cardsContainer.getChildren().add(emptyLbl);
        }
    }

    @FXML
    private void handleHomeTab(ActionEvent event) {
        SceneManager.switchScene(AppConfig.VIEW_STUDENT_DASHBOARD, AppConfig.STYLE_DASHBOARD);
    }

    @FXML
    private void handleFieldsTab(ActionEvent event) {
        System.out.println("Navigating to Fields tab...");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.logout();
        SceneManager.switchScene(AppConfig.VIEW_AUTH, AppConfig.STYLE_AUTH);
    }
}