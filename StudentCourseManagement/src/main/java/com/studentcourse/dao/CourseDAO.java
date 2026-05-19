package com.studentcourse.dao;

import com.studentcourse.model.Course;
import com.studentcourse.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course operations.
 * Handles all CRUD database queries for the courses table.
 */
public class CourseDAO {

    /**
     * Retrieves all courses from the database.
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("CourseDAO.getAllCourses() error: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Retrieves a single course by ID.
     */
    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        Course course = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    course = mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("CourseDAO.getCourseById() error: " + e.getMessage());
            e.printStackTrace();
        }

        return course;
    }

    /**
     * Inserts a new course into the database.
     */
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_name, duration, fees, trainer_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getDuration());
            ps.setDouble(3, course.getFees());
            ps.setString(4, course.getTrainerName());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("CourseDAO.addCourse() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates an existing course in the database.
     */
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_name = ?, duration = ?, fees = ?, trainer_name = ? WHERE course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getDuration());
            ps.setDouble(3, course.getFees());
            ps.setString(4, course.getTrainerName());
            ps.setInt(5, course.getCourseId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("CourseDAO.updateCourse() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a course by ID.
     */
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("CourseDAO.deleteCourse() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Returns the total number of courses.
     */
    public int getCourseCount() {
        String sql = "SELECT COUNT(*) FROM courses";
        int count = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("CourseDAO.getCourseCount() error: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Checks if a course has any active registrations (to prevent deletion).
     */
    public boolean hasActiveRegistrations(int courseId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("CourseDAO.hasActiveRegistrations() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a duplicate course exists with all matching fields (for Add).
     */
    public boolean isDuplicateCourse(String courseName, String duration, double fees, String trainerName) {
        String sql = "SELECT COUNT(*) FROM courses WHERE LOWER(course_name) = LOWER(?) AND LOWER(duration) = LOWER(?) AND fees = ? AND LOWER(trainer_name) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseName);
            ps.setString(2, duration);
            ps.setDouble(3, fees);
            ps.setString(4, trainerName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("CourseDAO.isDuplicateCourse() error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a duplicate course exists with all matching fields, excluding a specific course ID (for Update).
     */
    public boolean isDuplicateCourse(String courseName, String duration, double fees, String trainerName, int excludeCourseId) {
        String sql = "SELECT COUNT(*) FROM courses WHERE LOWER(course_name) = LOWER(?) AND LOWER(duration) = LOWER(?) AND fees = ? AND LOWER(trainer_name) = LOWER(?) AND course_id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseName);
            ps.setString(2, duration);
            ps.setDouble(3, fees);
            ps.setString(4, trainerName);
            ps.setInt(5, excludeCourseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("CourseDAO.isDuplicateCourse(excludeId) error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Maps a ResultSet row to a Course object.
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseName(rs.getString("course_name"));
        course.setDuration(rs.getString("duration"));
        course.setFees(rs.getDouble("fees"));
        course.setTrainerName(rs.getString("trainer_name"));
        return course;
    }
}
