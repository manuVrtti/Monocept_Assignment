package com.studentcourse.model;

/**
 * Model class representing a Student.
 * Maps to the 'students' table in the database.
 */
public class Student {

    private int studentId;
    private String studentName;
    private String email;
    private String phone;
    private int age;
    private String city;

    // Default constructor
    public Student() {
    }

    // Parameterized constructor
    public Student(int studentId, String studentName, String email, String phone, int age, String city) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.city = city;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
