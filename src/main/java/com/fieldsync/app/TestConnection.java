package com.fieldsync.app;

import com.fieldsync.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("[TEST 1] Initiating PostgreSQL Connection...");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            if (conn != null && !conn.isClosed()) {
                System.out.println("[SUCCESS] Database Connection Established!");

                System.out.println("\n[TEST 2] Fetching Seed Data from 'users' table...");
                ResultSet rs = stmt.executeQuery("SELECT user_id, full_name, role, department FROM users;");

                while (rs.next()) {
                    System.out.printf(" -> ID: %d | Name: %s | Role: %s | Dept: %s%n",
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getString("department"));
                }
                System.out.println("\n[STATUS] Database setup and connection test PASSED.");
            }
        } catch (Exception e) {
            System.err.println("\n[ERROR] Connection or Query Failed!");
            e.printStackTrace();
        }
    }
}