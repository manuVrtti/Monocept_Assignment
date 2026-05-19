package com.studentcourse.model;

/**
 * Model class representing a Student-Course Registration.
 * Maps to the 'registrations' table in the database.
 * Includes studentName and courseName for display purposes (from JOINs).
 */
public class Registration {

    private int registrationId;
    private int studentId;
    private int courseId;
    private String registrationDate;
    private String status;

    // Display fields (populated via JOIN queries)
    private String studentName;
    private String courseName;

    // Default constructor
    public Registration() {
    }

    // Parameterized constructor
    public Registration(int registrationId, int studentId, int courseId,
                        String registrationDate, String status) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
