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
 * EditStudentServlet - Handles GET /student/edit?id=X
 * Loads the student data and forwards to student-edit.jsp.
 */
public class EditStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("EditStudentServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("EditStudentServlet: doGet() called - Loading student for edit.");

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
            Student student = studentDAO.getStudentById(studentId);

            if (student != null) {
                request.setAttribute("student", student);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-edit.jsp");
                dispatcher.forward(request, response);
            } else {
                // Record not found
                request.setAttribute("error", "Student not found with ID: " + studentId);
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
        System.out.println("EditStudentServlet: destroy() called - Servlet destroyed.");
    }
}
