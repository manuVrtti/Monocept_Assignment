package com.studentcourse.controller;

import com.studentcourse.dao.CourseDAO;
import com.studentcourse.dao.RegistrationDAO;
import com.studentcourse.dao.StudentDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * DashboardServlet - Handles GET /dashboard
 * Fetches summary counts and forwards to dashboard.jsp.
 * Protected: requires active session.
 */
public class DashboardServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("DashboardServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DashboardServlet: doGet() called - Loading dashboard.");

        // Session check - redirect to login if not authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Fetch dashboard data
        StudentDAO studentDAO = new StudentDAO();
        CourseDAO courseDAO = new CourseDAO();
        RegistrationDAO registrationDAO = new RegistrationDAO();

        int studentCount = studentDAO.getStudentCount();
        int courseCount = courseDAO.getCourseCount();
        int registrationCount = registrationDAO.getRegistrationCount();

        request.setAttribute("studentCount", studentCount);
        request.setAttribute("courseCount", courseCount);
        request.setAttribute("registrationCount", registrationCount);
        request.setAttribute("adminUsername", session.getAttribute("adminUsername"));

        // Forward to dashboard.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("DashboardServlet: destroy() called - Servlet destroyed.");
    }
}
