package com.fieldsync.database;

import com.fieldsync.config.DatabaseConnection; 
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream("/schema.sql")) {
            if (inputStream == null) {
                System.err.println("[DatabaseInitializer] Could not find schema.sql in resources.");
                return;
            }

            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("[DatabaseInitializer] Database schema and initial seed initialized successfully.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}