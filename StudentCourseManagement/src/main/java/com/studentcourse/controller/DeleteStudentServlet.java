package com.studentcourse.controller;

import com.studentcourse.dao.StudentDAO;
import com.studentcourse.model.Student;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * DeleteStudentServlet - Handles GET /student/delete?id=X
 * Checks if student has registrations before deleting.
 * Per SRS: Cannot delete student if registered for any course.
 */
public class DeleteStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("DeleteStudentServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DeleteStudentServlet: doGet() called - Processing student delete.");

        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        try {
            int studentId = Integer.parseInt(idStr.trim());
            StudentDAO studentDAO = new StudentDAO();

            // Check if student has registrations
            if (studentDAO.hasRegistrations(studentId)) {
                // Cannot delete - has linked records
                request.setAttribute("error", "Cannot delete student. Student is registered for one or more courses. Please remove registrations first.");
                request.setAttribute("students", studentDAO.getAllStudents());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-list.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Safe to delete
            boolean success = studentDAO.deleteStudent(studentId);

            if (success) {
                session.setAttribute("toastMessage", "Student deleted successfully!");
                session.setAttribute("toastType", "success");
                response.sendRedirect(request.getContextPath() + "/students");
            } else {
                request.setAttribute("error", "Failed to delete student. Student not found.");
                request.setAttribute("students", studentDAO.getAllStudents());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-list.jsp");
                dispatcher.forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/students");
        }
    }

    @Override
    public void destroy() {
        System.out.println("DeleteStudentServlet: destroy() called - Servlet destroyed.");
    }
}
