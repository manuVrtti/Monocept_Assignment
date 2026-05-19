package com.studentcourse.dao;

import com.studentcourse.model.Student;
import com.studentcourse.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations.
 * Handles all CRUD database queries for the students table.
 */
public class StudentDAO {

    /**
     * Retrieves all students from the database.
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.getAllStudents() error: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    /**
     * Retrieves a single student by ID.
     */
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        Student student = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.getStudentById() error: " + e.getMessage());
            e.printStackTrace();
        }

        return student;
    }

    /**
     * Inserts a new student into the database.
     */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_name, email, phone, age, city) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getStudentName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPhone());
            ps.setInt(4, student.getAge());
            ps.setString(5, student.getCity());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("StudentDAO.addStudent() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates an existing student in the database.
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET student_name = ?, email = ?, phone = ?, age = ?, city = ? WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getStudentName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPhone());
            ps.setInt(4, student.getAge());
            ps.setString(5, student.getCity());
            ps.setInt(6, student.getStudentId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("StudentDAO.updateStudent() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a student by ID.
     */
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("StudentDAO.deleteStudent() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Returns the total number of students.
     */
    public int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM students";
        int count = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.getStudentCount() error: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Checks if a student has any registrations (to prevent deletion).
     */
    public boolean hasRegistrations(int studentId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.hasRegistrations() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if an email already exists (for Add - no exclusion).
     */
    public boolean isEmailExists(String email) {
        return isEmailExists(email, 0);
    }

    /**
     * Checks if an email already exists, excluding a specific student ID (for Update).
     */
    public boolean isEmailExists(String email, int excludeStudentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ? AND student_id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, excludeStudentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.isEmailExists() error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a phone number already exists (for Add - no exclusion).
     */
    public boolean isPhoneExists(String phone) {
        return isPhoneExists(phone, 0);
    }

    /**
     * Checks if a phone number already exists, excluding a specific student ID (for Update).
     */
    public boolean isPhoneExists(String phone, int excludeStudentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE phone = ? AND student_id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setInt(2, excludeStudentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.isPhoneExists() error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a student name already exists (for Add - no exclusion).
     */
    public boolean isStudentNameExists(String studentName) {
        return isStudentNameExists(studentName, 0);
    }

    /**
     * Checks if a student name already exists, excluding a specific student ID (for Update).
     */
    public boolean isStudentNameExists(String studentName, int excludeStudentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_name = ? AND student_id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentName);
            ps.setInt(2, excludeStudentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("StudentDAO.isStudentNameExists() error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Student object.
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setStudentName(rs.getString("student_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setAge(rs.getInt("age"));
        student.setCity(rs.getString("city"));
        return student;
    }
}
