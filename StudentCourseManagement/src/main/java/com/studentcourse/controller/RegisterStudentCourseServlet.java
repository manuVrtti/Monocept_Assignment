package com.studentcourse.controller;

import com.studentcourse.dao.CourseDAO;
import com.studentcourse.dao.RegistrationDAO;
import com.studentcourse.dao.StudentDAO;
import com.studentcourse.model.Registration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * RegisterStudentCourseServlet - Handles POST /registration/register
 * Validates registration data and inserts into database.
 * Checks for duplicate active registrations.
 */
public class RegisterStudentCourseServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("RegisterStudentCourseServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("RegisterStudentCourseServlet: doPost() called - Processing registration.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String studentIdStr = request.getParameter("studentId");
        String courseIdStr = request.getParameter("courseId");
        String registrationDate = request.getParameter("registrationDate");
        String status = request.getParameter("status");

        // Validation
        StringBuilder error = new StringBuilder();

        int studentId = 0;
        int courseId = 0;

        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            error.append("Student is required. ");
        } else {
            try {
                studentId = Integer.parseInt(studentIdStr.trim());
            } catch (NumberFormatException e) {
                error.append("Invalid student selection. ");
            }
        }

        if (courseIdStr == null || courseIdStr.trim().isEmpty()) {
            error.append("Course is required. ");
        } else {
            try {
                courseId = Integer.parseInt(courseIdStr.trim());
            } catch (NumberFormatException e) {
                error.append("Invalid course selection. ");
            }
        }

        if (registrationDate == null || registrationDate.trim().isEmpty()) {
            error.append("Registration Date is required. ");
        }

        if (status == null || status.trim().isEmpty()) {
            error.append("Status is required. ");
        }

        // Check for duplicate active registration
        if (error.length() == 0) {
            RegistrationDAO registrationDAO = new RegistrationDAO();
            if (registrationDAO.isDuplicateActiveRegistration(studentId, courseId)) {
                error.append("Duplicate active registration! This student is already actively registered for this course.");
            }
        }

        // If validation fails, forward back with error
        if (error.length() > 0) {
            request.setAttribute("error", error.toString().trim());
            request.setAttribute("selectedStudentId", studentIdStr);
            request.setAttribute("selectedCourseId", courseIdStr);
            request.setAttribute("registrationDate", registrationDate);
            request.setAttribute("selectedStatus", status);

            // Reload dropdown data
            StudentDAO studentDAO = new StudentDAO();
            CourseDAO courseDAO = new CourseDAO();
            request.setAttribute("students", studentDAO.getAllStudents());
            request.setAttribute("courses", courseDAO.getAllCourses());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registration-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Create and insert registration
        Registration registration = new Registration();
        registration.setStudentId(studentId);
        registration.setCourseId(courseId);
        registration.setRegistrationDate(registrationDate.trim());
        registration.setStatus(status.trim());

        RegistrationDAO registrationDAO = new RegistrationDAO();
        boolean success = registrationDAO.addRegistration(registration);

        if (success) {
            session.setAttribute("toastMessage", "Registration created successfully!");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/registrations");
        } else {
            request.setAttribute("error", "Failed to create registration. Please try again.");
            StudentDAO studentDAO = new StudentDAO();
            CourseDAO courseDAO = new CourseDAO();
            request.setAttribute("students", studentDAO.getAllStudents());
            request.setAttribute("courses", courseDAO.getAllCourses());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registration-form.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("RegisterStudentCourseServlet: destroy() called - Servlet destroyed.");
    }
}
