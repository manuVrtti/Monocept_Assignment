package com.studentcourse.controller;

import com.studentcourse.dao.RegistrationDAO;
import com.studentcourse.model.Registration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * ViewRegistrationsServlet - Handles GET /registrations
 * Fetches all registrations with joined student/course names.
 */
public class ViewRegistrationsServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("ViewRegistrationsServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("ViewRegistrationsServlet: doGet() called - Loading registration list.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        RegistrationDAO registrationDAO = new RegistrationDAO();
        List<Registration> registrations = registrationDAO.getAllRegistrations();

        request.setAttribute("registrations", registrations);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registration-list.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("ViewRegistrationsServlet: destroy() called - Servlet destroyed.");
    }
}
