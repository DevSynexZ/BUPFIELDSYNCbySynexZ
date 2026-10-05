package com.fieldsync.dao;

import com.fieldsync.config.DatabaseConnection;
import com.fieldsync.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    /**
     * Creates a notification for a target user (e.g., Registrar or Student).
     */
    public boolean createNotification(int userId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, message);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error creating notification: " + e.getMessage());
            return false;
        }
    }

    public List<Notification> getUnreadNotificationsByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getInt("user_id"),
                            rs.getString("message"),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error fetching notifications: " + e.getMessage());
        }
        return list;
    }
}