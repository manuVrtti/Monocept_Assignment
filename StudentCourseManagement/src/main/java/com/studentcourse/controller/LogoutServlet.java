package com.studentcourse.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LogoutServlet - Handles GET /logout
 * Invalidates the session and redirects to login page.
 */
public class LogoutServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("LogoutServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("LogoutServlet: doGet() called - Logging out admin.");

        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirect to login page with logout message
        response.sendRedirect(request.getContextPath() + "/login?toast=loggedout");
    }

    @Override
    public void destroy() {
        System.out.println("LogoutServlet: destroy() called - Servlet destroyed.");
    }
}
