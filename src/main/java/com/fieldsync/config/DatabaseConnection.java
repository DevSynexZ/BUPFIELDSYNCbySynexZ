package com.fieldsync.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/field_sync_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "wrongpassword43";

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

// package com.fieldsync.config;

// import io.github.cdimascio.dotenv.Dotenv;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

// public class DatabaseConnection {

//     // Load the .env file from the project root
//     private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

//     // Pull from .env, with local fallbacks if .env is missing
//     private static final String URL = dotenv.get("DB_URL") != null
//             ? dotenv.get("DB_URL")
//             : "jdbc:postgresql://localhost:5432/field_sync_db";

//     private static final String USER = dotenv.get("DB_USER") != null
//             ? dotenv.get("DB_USER")
//             : "postgres";

//     private static final String PASSWORD = dotenv.get("DB_PASSWORD") != null
//             ? dotenv.get("DB_PASSWORD")
//             : "xxxxxxxx";

//     private static Connection connection = null;

//     private DatabaseConnection() {
//     }

//     public static Connection getConnection() throws SQLException {
//         if (connection == null || connection.isClosed()|| !connection.isValid(2)) {
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