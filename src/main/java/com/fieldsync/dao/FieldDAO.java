
package com.fieldsync.dao;

import com.fieldsync.config.DatabaseConnection;
import com.fieldsync.model.Field;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FieldDAO {

    public List<Field> getAllActiveFields() {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT * FROM fields WHERE UPPER(status) = 'AVAILABLE' ORDER BY field_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fields.add(extractFieldFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[FieldDAO] Error fetching active fields: " + e.getMessage());
        }
        return fields;
    }

    /**
     * Fetches a field by its primary key ID.
     */
    public Field getFieldById(int fieldId) {
        String sql = "SELECT * FROM fields WHERE field_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fieldId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFieldFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[FieldDAO] Error fetching field by ID: " + e.getMessage());
        }
        return null;
    }

    private Field extractFieldFromResultSet(ResultSet rs) throws SQLException {
        String status = rs.getString("status");
        boolean isActive = status != null && status.equalsIgnoreCase("AVAILABLE");

        return new Field(
                rs.getInt("field_id"),
                rs.getString("field_name"),
                rs.getString("location"),
                isActive
        );
    }
}