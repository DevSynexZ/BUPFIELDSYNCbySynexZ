package com.fieldsync.dao;

import com.fieldsync.config.DatabaseConnection;
import com.fieldsync.model.MatchRoster;
import com.fieldsync.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
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
        private final String purpose;

        public ReservationDisplayDTO(int reservationId, String fieldName, String date, String timeSlot, String status, String purpose) {
            this.reservationId = reservationId;
            this.fieldName = fieldName;
            this.date = date;
            this.timeSlot = timeSlot;
            this.status = status;
            this.purpose = purpose;
        }

        public int getReservationId() { return reservationId; }
        public String getFieldName() { return fieldName; }
        public String getDate() { return date; }
        public String getTimeSlot() { return timeSlot; }
        public String getStatus() { return status; }
        public String getPurpose() { return purpose; }
    }

    public static class PendingApprovalDTO {
        private final int reservationId;
        private final int applicantId;
        private final String applicantEmail;
        private final String fullName;
        private final String department;
        private final String fieldName;
        private final String dateTimeSlot;
        private final String purpose;

        public PendingApprovalDTO(int reservationId, int applicantId, String applicantEmail, String fullName, String department, String fieldName, String dateTimeSlot, String purpose) {
            this.reservationId = reservationId;
            this.applicantId = applicantId;
            this.applicantEmail = applicantEmail;
            this.fullName = fullName;
            this.department = department;
            this.fieldName = fieldName;
            this.dateTimeSlot = dateTimeSlot;
            this.purpose = purpose;
        }

        public int getReservationId() { return reservationId; }
        public int getApplicantId() { return applicantId; }
        public String getApplicantEmail() { return applicantEmail; }
        public String getFullName() { return fullName; }
        public String getDepartment() { return department; }
        public String getFieldName() { return fieldName; }
        public String getDateTimeSlot() { return dateTimeSlot; }
        public String getPurpose() { return purpose; }
    }

    public List<PendingApprovalDTO> getAllPendingReservations() {
        List<PendingApprovalDTO> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.reserved_by, u.email, u.full_name, u.department, f.field_name, r.start_time, r.end_time, r.purpose " +
                     "FROM reservations r " +
                     "JOIN users u ON r.reserved_by = u.user_id " +
                     "JOIN fields f ON r.field_id = f.field_id " +
                     "WHERE r.status = 'PENDING_APPROVAL'::reservation_status " +
                     "ORDER BY r.created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();
                String slot = start.format(DATE_FORMATTER) + " (" + start.format(TIME_FORMATTER) + " - " + end.format(TIME_FORMATTER) + ")";

                list.add(new PendingApprovalDTO(
                    rs.getInt("reservation_id"),
                    rs.getInt("reserved_by"),
                    rs.getString("email"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getString("field_name"),
                    slot,
                    rs.getString("purpose")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching pending reservations: " + e.getMessage());
        }
        return list;
    }

    public List<Reservation> getReservationsByFieldAndDate(int fieldId, LocalDate date) {
        List<Reservation> list = new ArrayList<>();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        String sql = "SELECT reservation_id, field_id, reserved_by, approved_by, start_time, end_time, status, purpose, created_at "
                + "FROM reservations "
                + "WHERE field_id = ? AND start_time >= ? AND start_time < ? "
                + " AND status IN ('CONFIRMED'::reservation_status, 'PENDING_APPROVAL'::reservation_status) "
                + "ORDER BY start_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fieldId);
            stmt.setTimestamp(2, Timestamp.valueOf(startOfDay));
            stmt.setTimestamp(3, Timestamp.valueOf(endOfDay));

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
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching reservations by field and date: " + e.getMessage());
        }
        return list;
    }

    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT reservation_id, field_id, reserved_by, approved_by, start_time, end_time, status, purpose, created_at "
                + "FROM reservations WHERE reserved_by = ? ORDER BY start_time DESC";

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
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching reservations by user: " + e.getMessage());
        }
        return list;
    }

    public boolean hasOverlap(int fieldId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE field_id = ? " +
                "  AND status IN ('CONFIRMED'::reservation_status, 'PENDING_APPROVAL'::reservation_status) " +
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

    public boolean updateStatus(int reservationId, String newStatus, int approvedBy) {
        String sql = "UPDATE reservations SET status = ?::reservation_status, approved_by = ?, updated_at = NOW() WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, approvedBy);
            stmt.setInt(3, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error updating status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateReservationStatus(int reservationId, int userId, String newStatus, String fieldName) {
        String updateSql = "UPDATE reservations SET status = ?::reservation_status, updated_at = NOW() WHERE reservation_id = ?";
        String notifySql = "INSERT INTO notifications (user_id, title, message) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateSql)) {
                stmtUpdate.setString(1, newStatus);
                stmtUpdate.setInt(2, reservationId);
                stmtUpdate.executeUpdate();
            }

            try (PreparedStatement stmtNotify = conn.prepareStatement(notifySql)) {
                stmtNotify.setInt(1, userId);
                stmtNotify.setString(2, "Reservation " + newStatus);
                stmtNotify.setString(3, "Your request for " + fieldName + " has been " + newStatus.toLowerCase() + " by the Registrar.");
                stmtNotify.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("[ReservationDAO] Error updating reservation status: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

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

    public List<ReservationDisplayDTO> getReservationDisplayList(int userId) {
        List<ReservationDisplayDTO> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, f.field_name, r.start_time, r.end_time, r.status, r.purpose " +
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
                            rs.getString("status"),
                            rs.getString("purpose")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching display reservations: " + e.getMessage());
        }
        return list;
    }

    public List<ReservationDisplayDTO> getUpcomingConfirmedMatches(int userId) {
        List<ReservationDisplayDTO> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, f.field_name, r.start_time, r.end_time, r.status, r.purpose " +
                "FROM reservations r " +
                "JOIN fields f ON r.field_id = f.field_id " +
                "WHERE r.reserved_by = ? AND r.status = 'CONFIRMED'::reservation_status AND r.start_time >= NOW() " +
                "ORDER BY r.start_time ASC";

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
                            rs.getString("status"),
                            rs.getString("purpose")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching upcoming confirmed matches: " + e.getMessage());
        }
        return list;
    }

    public MatchRoster getRosterByReservationId(int reservationId) {
        String rosterSql = "SELECT roster_id, captain_name, manager_name FROM match_rosters WHERE reservation_id = ?";
        String playerSql = "SELECT player_name FROM roster_players WHERE roster_id = ? ORDER BY player_id ASC";

        MatchRoster roster = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement rStmt = conn.prepareStatement(rosterSql)) {

            rStmt.setInt(1, reservationId);
            try (ResultSet rs = rStmt.executeQuery()) {
                if (rs.next()) {
                    int rosterId = rs.getInt("roster_id");
                    roster = new MatchRoster();
                    roster.setRosterId(rosterId);
                    roster.setReservationId(reservationId);
                    roster.setCaptainName(rs.getString("captain_name"));
                    roster.setManagerName(rs.getString("manager_name"));

                    try (PreparedStatement pStmt = conn.prepareStatement(playerSql)) {
                        pStmt.setInt(1, rosterId);
                        try (ResultSet pRs = pStmt.executeQuery()) {
                            while (pRs.next()) {
                                roster.getPlayers().add(pRs.getString("player_name"));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error fetching match roster: " + e.getMessage());
        }
        return roster;
    }

    public boolean saveRoster(int reservationId, String captain, String manager, List<String> players) {
        String upsertRosterSql = "INSERT INTO match_rosters (reservation_id, captain_name, manager_name) " +
                "VALUES (?, ?, ?) ON CONFLICT (reservation_id) DO UPDATE " +
                "SET captain_name = EXCLUDED.captain_name, manager_name = EXCLUDED.manager_name, updated_at = CURRENT_TIMESTAMP " +
                "RETURNING roster_id;";

        String deletePlayersSql = "DELETE FROM roster_players WHERE roster_id = ?;";
        String insertPlayerSql = "INSERT INTO roster_players (roster_id, player_name) VALUES (?, ?);";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int rosterId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(upsertRosterSql)) {
                pstmt.setInt(1, reservationId);
                pstmt.setString(2, captain);
                pstmt.setString(3, manager);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    rosterId = rs.getInt("roster_id");
                }
            }

            if (rosterId != -1) {
                try (PreparedStatement delStmt = conn.prepareStatement(deletePlayersSql)) {
                    delStmt.setInt(1, rosterId);
                    delStmt.executeUpdate();
                }

                try (PreparedStatement insStmt = conn.prepareStatement(insertPlayerSql)) {
                    for (String player : players) {
                        if (player != null && !player.trim().isEmpty()) {
                            insStmt.setInt(1, rosterId);
                            insStmt.setString(2, player.trim());
                            insStmt.addBatch();
                        }
                    }
                    insStmt.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] Error saving roster: " + e.getMessage());
            return false;
        }
    }

    public int getActiveReservationsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reserved_by = ? AND status = 'CONFIRMED'::reservation_status";
        return executeCountQuery(sql, userId);
    }

    public int getPendingApprovalsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reserved_by = ? AND status = 'PENDING_APPROVAL'::reservation_status";
        return executeCountQuery(sql, userId);
    }

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