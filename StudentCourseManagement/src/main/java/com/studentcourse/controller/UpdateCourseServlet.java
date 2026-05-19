package com.studentcourse.controller;

import com.studentcourse.dao.CourseDAO;
import com.studentcourse.model.Course;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * UpdateCourseServlet - Handles POST /course/update
 * Validates input and updates course in the database.
 */
public class UpdateCourseServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("UpdateCourseServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("UpdateCourseServlet: doPost() called - Processing course update.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String courseIdStr = request.getParameter("courseId");
        String courseName = request.getParameter("courseName");
        String duration = request.getParameter("duration");
        String feesStr = request.getParameter("fees");
        String trainerName = request.getParameter("trainerName");

        StringBuilder error = new StringBuilder();

        if (courseName == null || courseName.trim().isEmpty()) {
            error.append("Course Name is required. ");
        }
        if (duration == null || duration.trim().isEmpty()) {
            error.append("Duration is required. ");
        }
        if (trainerName == null || trainerName.trim().isEmpty()) {
            error.append("Trainer Name is required. ");
        }

        int courseId = 0;
        double fees = 0;

        try {
            courseId = Integer.parseInt(courseIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/courses");
            return;
        }

        if (feesStr == null || feesStr.trim().isEmpty()) {
            error.append("Fees is required. ");
        } else {
            try {
                fees = Double.parseDouble(feesStr.trim());
                if (fees <= 0) {
                    error.append("Fees must be greater than 0. ");
                }
            } catch (NumberFormatException e) {
                error.append("Fees must be a valid number. ");
            }
        }

        if (error.length() > 0) {
            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseName(courseName);
            course.setDuration(duration);
            course.setFees(fees);
            course.setTrainerName(trainerName);

            request.setAttribute("error", error.toString().trim());
            request.setAttribute("course", course);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-edit.jsp");
            dispatcher.forward(request, response);
            return;
        }

        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseName(courseName.trim());
        course.setDuration(duration.trim());
        course.setFees(fees);
        course.setTrainerName(trainerName.trim());

        CourseDAO courseDAO = new CourseDAO();

        // Check for duplicate course (all fields match, exclude self)
        if (courseDAO.isDuplicateCourse(courseName.trim(), duration.trim(), fees, trainerName.trim(), courseId)) {
            request.setAttribute("error", "A course with the same name, duration, fees, and trainer already exists.");
            request.setAttribute("course", course);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-edit.jsp");
            dispatcher.forward(request, response);
            return;
        }

        boolean success = courseDAO.updateCourse(course);

        if (success) {
            session.setAttribute("toastMessage", "Course updated successfully!");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/courses");
        } else {
            request.setAttribute("error", "Failed to update course. Please try again.");
            request.setAttribute("course", course);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-edit.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("UpdateCourseServlet: destroy() called - Servlet destroyed.");
    }
}
