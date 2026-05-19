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
 * AddCourseServlet - Handles GET and POST /course/add
 * GET: Shows the add course form.
 * POST: Validates input and inserts course into database.
 */
public class AddCourseServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("AddCourseServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AddCourseServlet: doGet() called - Showing add course form.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-form.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AddCourseServlet: doPost() called - Processing add course.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String courseName = request.getParameter("courseName");
        String duration = request.getParameter("duration");
        String feesStr = request.getParameter("fees");
        String trainerName = request.getParameter("trainerName");

        // Validation
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

        double fees = 0;
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
            request.setAttribute("error", error.toString().trim());
            request.setAttribute("courseName", courseName);
            request.setAttribute("duration", duration);
            request.setAttribute("fees", feesStr);
            request.setAttribute("trainerName", trainerName);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        Course course = new Course();
        course.setCourseName(courseName.trim());
        course.setDuration(duration.trim());
        course.setFees(fees);
        course.setTrainerName(trainerName.trim());

        CourseDAO courseDAO = new CourseDAO();

        // Check for duplicate course (all fields match)
        if (courseDAO.isDuplicateCourse(courseName.trim(), duration.trim(), fees, trainerName.trim())) {
            request.setAttribute("error", "A course with the same name, duration, fees, and trainer already exists.");
            request.setAttribute("courseName", courseName);
            request.setAttribute("duration", duration);
            request.setAttribute("fees", feesStr);
            request.setAttribute("trainerName", trainerName);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        boolean success = courseDAO.addCourse(course);

        if (success) {
            HttpSession sess = request.getSession();
            sess.setAttribute("toastMessage", "Course '" + courseName.trim() + "' added successfully!");
            sess.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/courses");
        } else {
            request.setAttribute("error", "Failed to add course. Please try again.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-form.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("AddCourseServlet: destroy() called - Servlet destroyed.");
    }
}
