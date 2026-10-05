
package com.fieldsync.dao;

import com.fieldsync.config.DatabaseConnection;
import com.fieldsync.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

   
    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error during authentication: " + e.getMessage());
        }

        return null;
    }

    public boolean registerStudentRep(User user){
        String sql = "INSERT INTO users (full_name,email,password_hash,role,department) "+"VALUES (?,?,?, 'STUDENT_REP'::user_role, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,user.getName());
            stmt.setString(2,user.getEmail());
            stmt.setString(3,user.getPassword());
            stmt.setString(4,user.getDepartment());


            return stmt.executeUpdate()>0;
        }catch(SQLException e){
            System.err.println("[UserDAO] Registration Failed: "+ e.getMessage());
            return false;
        }

    }

    public boolean emailExists(String email){
        String sql = "SELECT COUNT(*) FROM users WHERE email=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Fetches a user by unique user_id.
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching user by ID: " + e.getMessage());
        }
        return null;
    }



    // Maps SQL ResultSet to User model matching exact schema column names
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("department")
        );
    }
}