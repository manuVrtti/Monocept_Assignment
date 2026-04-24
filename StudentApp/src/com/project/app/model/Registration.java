package com.project.app.model;

public class Registration {

    private int studentId;
    private int courseId;
    private double feesPaid;

    public Registration(int studentId, int courseId, double feesPaid) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.feesPaid = feesPaid;
    }

    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public double getFeesPaid() { return feesPaid; }
}