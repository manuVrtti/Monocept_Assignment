package com.studentcourse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for creating and returning database connections.
 * Centralizes all JDBC connection logic to avoid repeated code.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/student_course_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Suy3103";

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
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return connection;
    }
}
