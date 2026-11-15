// package com.fieldsync.config;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

// public class DatabaseConnection {

//     private static final String URL = "jdbc:postgresql://localhost:5432/field_sync_db";
//     private static final String USER = "postgres";
//     private static final String PASSWORD = "wrongpassword43";

//     private static Connection connection = null;

//     private DatabaseConnection() {
//     }

//     public static Connection getConnection() throws SQLException {
//         if (connection == null || connection.isClosed()) {
//             try {
//                 Class.forName("org.postgresql.Driver");
//                 connection = DriverManager.getConnection(URL, USER, PASSWORD);
//             } catch (ClassNotFoundException e) {
//                 throw new SQLException("PostgreSQL Driver not found in pom.xml classpath.", e);

//             }
//         }
//         return connection;
//     }
// }


package com.fieldsync.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Pulls from environment variables, falls back to local if not provided
    private static final String URL = System.getenv("DB_URL") != null 
        ? System.getenv("DB_URL") 
        : "jdbc:postgresql://localhost:5432/field_sync_db";
        
    private static final String USER = System.getenv("DB_USER") != null 
        ? System.getenv("DB_USER") 
        : "postgres";
        
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
        ? System.getenv("DB_PASSWORD") 
        : "xxxxxxxx";

    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL Driver not found in pom.xml classpath.", e);
            }
        }
        return connection;
    }
}