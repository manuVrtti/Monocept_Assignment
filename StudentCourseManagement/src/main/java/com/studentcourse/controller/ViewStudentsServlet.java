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
import java.util.List;

/**
 * ViewStudentsServlet - Handles GET /students
 * Fetches all students and forwards to student-list.jsp.
 */
public class ViewStudentsServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("ViewStudentsServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("ViewStudentsServlet: doGet() called - Loading student list.");

        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StudentDAO studentDAO = new StudentDAO();
        List<Student> students = studentDAO.getAllStudents();

        request.setAttribute("students", students);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("ViewStudentsServlet: destroy() called - Servlet destroyed.");
    }
}
