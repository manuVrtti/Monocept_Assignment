package com.studentcourse.dao;

import com.studentcourse.model.Registration;
import com.studentcourse.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Registration operations.
 * Handles all CRUD database queries for the registrations table.
 * Uses JOINs to populate student and course names for display.
 */
public class RegistrationDAO {

    /**
     * Retrieves all registrations with student and course names via JOIN.
     */
    public List<Registration> getAllRegistrations() {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.student_id, r.course_id, r.registration_date, r.status, "
                   + "s.student_name, c.course_name "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN courses c ON r.course_id = c.course_id "
                   + "ORDER BY r.registration_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Registration reg = new Registration();
                reg.setRegistrationId(rs.getInt("registration_id"));
                reg.setStudentId(rs.getInt("student_id"));
                reg.setCourseId(rs.getInt("course_id"));
                reg.setRegistrationDate(rs.getString("registration_date"));
                reg.setStatus(rs.getString("status"));
                reg.setStudentName(rs.getString("student_name"));
                reg.setCourseName(rs.getString("course_name"));
                registrations.add(reg);
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.getAllRegistrations() error: " + e.getMessage());
            e.printStackTrace();
        }

        return registrations;
    }

    /**
     * Inserts a new registration into the database.
     */
    public boolean addRegistration(Registration registration) {
        String sql = "INSERT INTO registrations (student_id, course_id, registration_date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, registration.getStudentId());
            ps.setInt(2, registration.getCourseId());
            ps.setString(3, registration.getRegistrationDate());
            ps.setString(4, registration.getStatus());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.addRegistration() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates the status of an existing registration.
     */
    public boolean updateStatus(int registrationId, String status) {
        String sql = "UPDATE registrations SET status = ? WHERE registration_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, registrationId);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.updateStatus() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a registration by ID.
     */
    public boolean deleteRegistration(int registrationId) {
        String sql = "DELETE FROM registrations WHERE registration_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, registrationId);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.deleteRegistration() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Returns the total number of registrations.
     */
    public int getRegistrationCount() {
        String sql = "SELECT COUNT(*) FROM registrations";
        int count = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.getRegistrationCount() error: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Checks if a duplicate active registration exists for the same student-course combination.
     * Per SRS: Duplicate Active Registration is not allowed.
     */
    public boolean isDuplicateActiveRegistration(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND course_id = ? AND status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("RegistrationDAO.isDuplicateActiveRegistration() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
