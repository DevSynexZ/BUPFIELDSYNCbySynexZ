package com.fieldsync.dao;

import com.fieldsync.config.DatabaseConnection;
import com.fieldsync.model.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

  
    public static class ReservationDisplayDTO {
        private final int reservationId;
        private final String fieldName;
        private final String date;
        private final String timeSlot;
        private final String status;

        public ReservationDisplayDTO(int reservationId, String fieldName, String date, String timeSlot, String status) {
            this.reservationId = reservationId;
            this.fieldName = fieldName;
            this.date = date;
            this.timeSlot = timeSlot;
            this.status = status;
        }

        public int getReservationId() { return reservationId; }
        public String getFieldName() { return fieldName; }
        public String getDate() { return date; }
        public String getTimeSlot() { return timeSlot; }
        public String getStatus() { return status; }
    }

    /**
     * Checks for time overlaps across existing bookings.
     */
    public boolean hasOverlap(int fieldId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                     "WHERE field_id = ? " +
                     "  AND status IN ('CONFIRMED', 'PENDING_APPROVAL') " +
                     "  AND (start_time < ? AND end_time > ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fieldId);
            stmt.setTimestamp(2, Timestamp.valueOf(endTime));
            stmt.setTimestamp(3, Timestamp.valueOf(startTime));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error checking overlap: " + e.getMessage());
        }
        return true;
    }

    /**
     * Creates a new booking request in PostgreSQL.
     */
    public boolean createReservation(Reservation reservation) {
        if (hasOverlap(reservation.getFieldId(), reservation.getStartTime(), reservation.getEndTime())) {
            System.err.println("[ReservationDAO] Cannot create reservation: Time slot overlaps!");
            return false;
        }

        String sql = "INSERT INTO reservations (field_id, reserved_by, start_time, end_time, status, purpose) " +
                     "VALUES (?, ?, ?, ?, 'PENDING_APPROVAL'::reservation_status, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getFieldId());
            stmt.setInt(2, reservation.getReservedBy());
            stmt.setTimestamp(3, Timestamp.valueOf(reservation.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getEndTime()));
            stmt.setString(5, reservation.getPurpose());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error creating reservation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches raw Reservation model entities by user ID for test validation.
     */
    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT reservation_id, field_id, reserved_by, approved_by, start_time, end_time, status, purpose, created_at " +
                     "FROM reservations WHERE reserved_by = ? ORDER BY start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer approvedBy = rs.getInt("approved_by");
                    if (rs.wasNull()) approvedBy = null;

                    list.add(new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("field_id"),
                        rs.getInt("reserved_by"),
                        approvedBy,
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getString("purpose"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching reservations by user: " + e.getMessage());
        }
        return list;
    }

    /**
     * Updates status and registrar approval for a reservation.
     */
    public boolean updateStatus(int reservationId, String newStatus, int registrarId) {
        String sql = "UPDATE reservations SET status = ?::reservation_status, approved_by = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, registrarId);
            stmt.setInt(3, reservationId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error updating reservation status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Counts active (CONFIRMED) reservations for a specific user.
     */
    public int getActiveReservationsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reserved_by = ? AND status = 'CONFIRMED'::reservation_status";
        return executeCountQuery(sql, userId);
    }

    /**
     * Counts pending requests for a specific user.
     */
    public int getPendingApprovalsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reserved_by = ? AND status = 'PENDING_APPROVAL'::reservation_status";
        return executeCountQuery(sql, userId);
    }

    /**
     * Gets the next upcoming confirmed match time.
     */
    public LocalDateTime getNextMatchTime(int userId) {
        String sql = "SELECT start_time FROM reservations " +
                     "WHERE reserved_by = ? AND status = 'CONFIRMED'::reservation_status AND start_time >= NOW() " +
                     "ORDER BY start_time ASC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("start_time").toLocalDateTime();
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching next match: " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches user reservations joined with field names for display in TableView.
     */
    public List<ReservationDisplayDTO> getReservationDisplayList(int userId) {
        List<ReservationDisplayDTO> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, f.field_name, r.start_time, r.end_time, r.status " +
                     "FROM reservations r " +
                     "JOIN fields f ON r.field_id = f.field_id " +
                     "WHERE r.reserved_by = ? " +
                     "ORDER BY r.start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                    LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();

                    String formattedDate = start.format(DATE_FORMATTER);
                    String formattedTimeSlot = start.format(TIME_FORMATTER) + " - " + end.format(TIME_FORMATTER);

                    list.add(new ReservationDisplayDTO(
                        rs.getInt("reservation_id"),
                        rs.getString("field_name"),
                        formattedDate,
                        formattedTimeSlot,
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching display reservations: " + e.getMessage());
        }
        return list;
    }

    private int executeCountQuery(String sql, int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error executing count query: " + e.getMessage());
        }
        return 0;
    }
}