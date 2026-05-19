package com.studentcourse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for creating and returning database connections.
 * Centralizes all JDBC connection logic to avoid repeated code.
 */
public class DBConnection {

    // Default local values
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/student_course_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "@Suy3103";

    // Cloud DB (Render/Aiven) Environment Variables
    private static final String DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DEFAULT_URL;
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : DEFAULT_USER;
    private static final String DB_PASS = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : DEFAULT_PASS;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("DBConnection: MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("DBConnection: MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Creates and returns a new database connection.
     *
     * @return Connection object to student_course_db
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        return connection;
    }
}
