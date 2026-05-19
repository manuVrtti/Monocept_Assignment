package com.studentcourse.dao;

import com.studentcourse.model.Admin;
import com.studentcourse.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Admin operations.
 * Handles all database queries related to admin authentication.
 */
public class AdminDAO {

    /**
     * Validates admin credentials against the database.
     *
     * @param username the admin username
     * @param password the admin password
     * @return Admin object if valid, null otherwise
     */
    public Admin validateAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.err.println("AdminDAO.validateAdmin() error: " + e.getMessage());
            e.printStackTrace();
        }

        return admin;
    }
}
